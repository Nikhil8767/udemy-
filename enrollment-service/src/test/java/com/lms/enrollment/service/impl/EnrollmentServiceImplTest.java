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
import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.entity.LessonProgress;
import com.lms.enrollment.enums.EnrollmentStatus;
import com.lms.common.exception.ForbiddenException;
import com.lms.common.exception.BusinessException;
import com.lms.enrollment.repository.EnrollmentRepository;
import com.lms.enrollment.repository.LessonProgressRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @Mock
    private ContentServiceClient contentServiceClient;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private ApiResponse<CourseResponse> setupCourseResponse(String status) {
        CourseResponse response = new CourseResponse();
        response.setCourseStatus(status);
        
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(response);
        return apiResponse;
    }

    @Test
    void enroll_ShouldSave_WhenStudentAndCourseIsPublished() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(courseId);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupCourseResponse("PUBLISHED"));

        enrollmentService.enroll(studentId.toString(), "ROLE_STUDENT", request);

        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void enroll_ShouldThrowAccessDenied_WhenNotStudent() {
        EnrollmentRequest request = new EnrollmentRequest();
        assertThrows(ForbiddenException.class, () -> 
            enrollmentService.enroll(UUID.randomUUID().toString(), "ROLE_TUTOR", request)
        );
    }

    @Test
    void enroll_ShouldThrowDuplicate_WhenAlreadyEnrolled() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(courseId);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> 
            enrollmentService.enroll(studentId.toString(), "ROLE_STUDENT", request)
        );
    }

    @Test
    void enroll_ShouldThrowBusinessException_WhenCourseNotPublished() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(courseId);

        when(enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)).thenReturn(false);
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupCourseResponse("DRAFT"));

        assertThrows(BusinessException.class, () -> 
            enrollmentService.enroll(studentId.toString(), "ROLE_STUDENT", request)
        );
    }

    @Test
    void markLessonCompleted_ShouldUpdateProgress_WhenValid() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();

        Enrollment enrollment = Enrollment.builder()
                .id(enrollmentId)
                .status(EnrollmentStatus.ENROLLED)
                .totalLessons(2)
                .completedLessons(0)
                .build();

        LessonProgress progress = LessonProgress.builder()
                .enrollment(enrollment)
                .completed(false)
                .build();

        LessonProgressRequest request = new LessonProgressRequest();
        request.setWatchTimeMinutes(10);
        request.setLastPositionSeconds(600);

        ApiResponse<LessonResponse> lessonResponse = new ApiResponse<>();
        lessonResponse.setSuccess(true);
        lessonResponse.setData(new LessonResponse());

        when(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.of(enrollment));
        when(contentServiceClient.getLesson(lessonId.toString())).thenReturn(lessonResponse);
        when(lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)).thenReturn(Optional.of(progress));

        boolean result = enrollmentService.markLessonCompleted(courseId.toString(), lessonId.toString(), studentId.toString(), "ROLE_STUDENT", request);

        assertThat(progress.isCompleted()).isTrue();
        assertThat(enrollment.getCompletedLessons()).isEqualTo(1);
        assertThat(enrollment.getProgressPercentage()).isEqualTo(50);
        assertThat(result).isFalse(); // Not fully completed
        verify(lessonProgressRepository, times(1)).save(progress);
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    void markLessonCompleted_ShouldMarkCourseCompleted_WhenAllLessonsDone() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();

        Enrollment enrollment = Enrollment.builder()
                .id(enrollmentId)
                .status(EnrollmentStatus.ENROLLED)
                .totalLessons(1)
                .completedLessons(0)
                .build();

        LessonProgress progress = LessonProgress.builder()
                .enrollment(enrollment)
                .completed(false)
                .build();

        LessonProgressRequest request = new LessonProgressRequest();

        ApiResponse<LessonResponse> lessonResponse = new ApiResponse<>();
        lessonResponse.setSuccess(true);
        lessonResponse.setData(new LessonResponse());

        when(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.of(enrollment));
        when(contentServiceClient.getLesson(lessonId.toString())).thenReturn(lessonResponse);
        when(lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)).thenReturn(Optional.of(progress));

        boolean result = enrollmentService.markLessonCompleted(courseId.toString(), lessonId.toString(), studentId.toString(), "ROLE_STUDENT", request);

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(result).isTrue(); 
    }

    @Test
    void dropCourse_ShouldChangeStatusToDropped() {
        UUID studentId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        Enrollment enrollment = Enrollment.builder()
                .status(EnrollmentStatus.ENROLLED)
                .build();

        when(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)).thenReturn(Optional.of(enrollment));

        enrollmentService.dropCourse(courseId.toString(), studentId.toString(), "ROLE_STUDENT");

        assertThat(enrollment.getStatus()).isEqualTo(EnrollmentStatus.DROPPED);
        verify(enrollmentRepository, times(1)).save(enrollment);
    }
}
