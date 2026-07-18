package com.lms.enrollment.service.impl;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.enrollment.client.ContentServiceClient;
import com.lms.enrollment.client.CourseServiceClient;
import com.lms.enrollment.dto.request.EnrollmentRequest;
import com.lms.enrollment.dto.request.LessonProgressRequest;
import com.lms.enrollment.dto.response.CourseResponse;
import com.lms.enrollment.dto.response.LessonResponse;
import com.lms.enrollment.dto.response.SectionResponse;
import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.entity.LessonProgress;
import com.lms.enrollment.enums.EnrollmentStatus;
import com.lms.common.exception.ForbiddenException;
import com.lms.common.exception.BusinessException;
import com.lms.enrollment.repository.EnrollmentRepository;
import com.lms.enrollment.repository.LessonProgressRepository;
import com.lms.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseServiceClient courseServiceClient;
    private final ContentServiceClient contentServiceClient;

    private void verifyStudentRole(String role) {
        if (!"ROLE_STUDENT".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Only students can enroll.");
        }
    }

    private void verifyCoursePublished(String courseId, String userId, String role) {
        try {
            ApiResponse<CourseResponse> courseResponse = courseServiceClient.getCourseDetails(courseId, userId, role);
            if (courseResponse == null || !courseResponse.isSuccess() || courseResponse.getData() == null) {
                throw new ResourceNotFoundException("Course not found.");
            }
            if (!"PUBLISHED".equalsIgnoreCase(courseResponse.getData().getCourseStatus())) {
                throw new BusinessException("Course is not published.");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Course not found.");
        }
    }

    private int calculateTotalLessons(String courseId) {
        int total = 0;
        try {
            ApiResponse<List<SectionResponse>> sections = contentServiceClient.getCourseSections(courseId);
            if (sections != null && sections.isSuccess() && sections.getData() != null) {
                for (SectionResponse section : sections.getData()) {
                    ApiResponse<List<LessonResponse>> lessons = contentServiceClient.getSectionLessons(section.getId().toString());
                    if (lessons != null && lessons.isSuccess() && lessons.getData() != null) {
                        total += lessons.getData().size();
                    }
                }
            }
        } catch (Exception e) {
            // Ignored, defaults to 0 safely
        }
        return total;
    }

    @Override
    @Transactional
    public void enroll(String userId, String role, EnrollmentRequest request) {
        verifyStudentRole(role);
        
        UUID studentId = UUID.fromString(userId);
        
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, request.getCourseId())) {
            return;
        }

        verifyCoursePublished(request.getCourseId().toString(), userId, role);

        int totalLessons = calculateTotalLessons(request.getCourseId().toString());

        Enrollment enrollment = Enrollment.builder()
                .studentId(studentId)
                .courseId(request.getCourseId())
                .totalLessons(totalLessons)
                .build();
        
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> getMyEnrollments(String userId, String role) {
        verifyStudentRole(role);
        return enrollmentRepository.findAllByStudentId(UUID.fromString(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Enrollment getEnrollmentDetails(String courseId, String userId, String role) {
        if (!"ROLE_STUDENT".equalsIgnoreCase(role)) {
            throw new ForbiddenException("Access denied.");
        }
        return enrollmentRepository.findByStudentIdAndCourseId(UUID.fromString(userId), UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCourseStudentCount(String courseId) {
        return enrollmentRepository.countByCourseIdAndStatusIn(
            UUID.fromString(courseId), 
            List.of(EnrollmentStatus.ENROLLED, EnrollmentStatus.IN_PROGRESS, EnrollmentStatus.COMPLETED)
        );
    }

    @Override
    @Transactional
    public boolean markLessonCompleted(String courseId, String lessonId, String userId, String role, LessonProgressRequest request) {
        verifyStudentRole(role);
        
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(UUID.fromString(userId), UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));

        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new BusinessException("Validation failed. Cannot update a dropped course.");
        }

        try {
            ApiResponse<LessonResponse> lesson = contentServiceClient.getLesson(lessonId);
            if (lesson == null || !lesson.isSuccess() || lesson.getData() == null) {
                throw new ResourceNotFoundException("Lesson not found.");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Lesson not found.");
        }

        LessonProgress progress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollment.getId(), UUID.fromString(lessonId))
                .orElse(LessonProgress.builder()
                        .enrollment(enrollment)
                        .lessonId(UUID.fromString(lessonId))
                        .build());

        if (!progress.isCompleted()) {
            progress.setCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            enrollment.setCompletedLessons(enrollment.getCompletedLessons() + 1);
        }

        progress.setWatchTimeMinutes(request.getWatchTimeMinutes());
        progress.setLastPositionSeconds(request.getLastPositionSeconds());

        lessonProgressRepository.save(progress);

        enrollment.setLastAccessedLessonId(UUID.fromString(lessonId));
        enrollment.setLastAccessedAt(LocalDateTime.now());
        enrollment.setStatus(EnrollmentStatus.IN_PROGRESS);

        boolean courseJustCompleted = false;

        if (enrollment.getTotalLessons() > 0) {
            int percentage = (int) (((double) enrollment.getCompletedLessons() / enrollment.getTotalLessons()) * 100);
            if (percentage > 100) percentage = 100;
            enrollment.setProgressPercentage(percentage);
            
            if (percentage == 100 && enrollment.getCompletedAt() == null) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletedAt(LocalDateTime.now());
                courseJustCompleted = true;
            } else if (percentage == 100) {
                courseJustCompleted = true;
            }
        }

        enrollmentRepository.save(enrollment);
        return courseJustCompleted;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getProgress(String courseId, String userId, String role) {
        Enrollment enrollment = getEnrollmentDetails(courseId, userId, role);
        return enrollment.getProgressPercentage();
    }

    @Override
    @Transactional
    public void dropCourse(String courseId, String userId, String role) {
        verifyStudentRole(role);
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(UUID.fromString(userId), UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found."));
        
        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional(readOnly = true)
    public com.lms.enrollment.dto.response.EnrollmentStatusDTO getEnrollmentStatus(String courseId, String userId, String role) {
        if (!"ROLE_STUDENT".equalsIgnoreCase(role)) {
            return com.lms.enrollment.dto.response.EnrollmentStatusDTO.builder().enrolled(false).build();
        }
        
        return enrollmentRepository.findByStudentIdAndCourseId(UUID.fromString(userId), UUID.fromString(courseId))
            .map(enrollment -> {
                UUID resumeLessonId = enrollment.getLastAccessedLessonId();
                if (resumeLessonId == null || isLessonCompleted(enrollment.getId(), resumeLessonId)) {
                    resumeLessonId = findFirstIncompleteLesson(courseId, enrollment.getId());
                    if (resumeLessonId == null) {
                        resumeLessonId = enrollment.getLastAccessedLessonId(); // fallback
                    }
                }
                
                return com.lms.enrollment.dto.response.EnrollmentStatusDTO.builder()
                        .enrolled(true)
                        .enrollmentId(enrollment.getId())
                        .progressPercentage(enrollment.getProgressPercentage())
                        .lastAccessedLessonId(resumeLessonId)
                        .completedLessons(enrollment.getCompletedLessons())
                        .enrollmentDate(enrollment.getEnrolledAt())
                        .build();
            })
            .orElse(com.lms.enrollment.dto.response.EnrollmentStatusDTO.builder().enrolled(false).build());
    }

    private boolean isLessonCompleted(UUID enrollmentId, UUID lessonId) {
        return lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .map(LessonProgress::isCompleted)
                .orElse(false);
    }

    private UUID findFirstIncompleteLesson(String courseId, UUID enrollmentId) {
        try {
            ApiResponse<List<SectionResponse>> sections = contentServiceClient.getCourseSections(courseId);
            if (sections != null && sections.isSuccess() && sections.getData() != null) {
                UUID lastLessonId = null;
                for (SectionResponse section : sections.getData()) {
                    ApiResponse<List<LessonResponse>> lessons = contentServiceClient.getSectionLessons(section.getId().toString());
                    if (lessons != null && lessons.isSuccess() && lessons.getData() != null) {
                        for (LessonResponse lesson : lessons.getData()) {
                            lastLessonId = lesson.getId();
                            if (!isLessonCompleted(enrollmentId, lesson.getId())) {
                                return lesson.getId();
                            }
                        }
                    }
                }
                return lastLessonId; // If all completed, return the very last lesson
            }
        } catch (Exception e) {
            // Log warning
        }
        return null;
    }
}
