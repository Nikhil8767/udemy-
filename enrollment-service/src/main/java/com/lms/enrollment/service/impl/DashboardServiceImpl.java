package com.lms.enrollment.service.impl;

import com.lms.enrollment.dto.response.EnrollmentResponse;
import com.lms.enrollment.dto.response.StudentDashboardResponse;
import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.enums.EnrollmentStatus;
import com.lms.enrollment.repository.EnrollmentRepository;
import com.lms.enrollment.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public StudentDashboardResponse getStudentDashboard(String userId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(UUID.fromString(userId));

        int active = 0;
        int completed = 0;

        for (Enrollment e : enrollments) {
            if (e.getStatus() == EnrollmentStatus.ENROLLED || e.getStatus() == EnrollmentStatus.IN_PROGRESS) {
                active++;
            } else if (e.getStatus() == EnrollmentStatus.COMPLETED) {
                completed++;
            }
        }

        List<EnrollmentResponse> recent = enrollments.stream()
                .filter(e -> e.getStatus() != EnrollmentStatus.DROPPED)
                .sorted((a, b) -> b.getEnrolledAt().compareTo(a.getEnrolledAt()))
                .limit(5)
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return StudentDashboardResponse.builder()
                .activeCourses(active)
                .completedCourses(completed)
                .totalEnrollments(enrollments.size())
                .recentEnrollments(recent)
                .build();
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudentId())
                .courseId(enrollment.getCourseId())
                .status(enrollment.getStatus())
                .progressPercentage(enrollment.getProgressPercentage())
                .completedLessons(enrollment.getCompletedLessons())
                .totalLessons(enrollment.getTotalLessons())
                .lastAccessedLessonId(enrollment.getLastAccessedLessonId())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .build();
    }
}
