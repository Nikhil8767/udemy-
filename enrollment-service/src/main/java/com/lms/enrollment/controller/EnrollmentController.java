package com.lms.enrollment.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.enrollment.dto.request.EnrollmentRequest;
import com.lms.enrollment.dto.request.LessonProgressRequest;
import com.lms.enrollment.dto.response.EnrollmentResponse;
import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Endpoints for managing course enrollments and progress")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    @Operation(summary = "Enroll in course")
    public ResponseEntity<ApiResponse<Void>> enroll(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody EnrollmentRequest request) {
        enrollmentService.enroll(userId, role, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Enrollment successful.")
                        .build()
        );
    }

    @GetMapping("/my")
    @Operation(summary = "Authenticated student's enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        List<EnrollmentResponse> responses = enrollmentService.getMyEnrollments(userId, role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<EnrollmentResponse>>builder()
                        .success(true)
                        .message("Enrollment details fetched successfully.")
                        .data(responses)
                        .build()
        );
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "Enrollment details")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentDetails(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        Enrollment enrollment = enrollmentService.getEnrollmentDetails(courseId, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<EnrollmentResponse>builder()
                        .success(true)
                        .message("Enrollment details fetched successfully.")
                        .data(mapToResponse(enrollment))
                        .build()
        );
    }

    @GetMapping("/{courseId}/status")
    @Operation(summary = "Get detailed enrollment status")
    public ResponseEntity<ApiResponse<com.lms.enrollment.dto.response.EnrollmentStatusDTO>> getEnrollmentStatus(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        com.lms.enrollment.dto.response.EnrollmentStatusDTO status = enrollmentService.getEnrollmentStatus(courseId, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<com.lms.enrollment.dto.response.EnrollmentStatusDTO>builder()
                        .success(true)
                        .message("Enrollment status fetched successfully.")
                        .data(status)
                        .build()
        );
    }

    @GetMapping("/{courseId}/count")
    @Operation(summary = "Get student count for course")
    public ResponseEntity<ApiResponse<Long>> getCourseStudentCount(
            @PathVariable String courseId) {
        Long count = enrollmentService.getCourseStudentCount(courseId);
        return ResponseEntity.ok(
                ApiResponse.<Long>builder()
                        .success(true)
                        .message("Student count fetched successfully.")
                        .data(count)
                        .build()
        );
    }

    @PatchMapping("/{courseId}/complete-lesson/{lessonId}")
    @Operation(summary = "Mark lesson completed")
    public ResponseEntity<ApiResponse<Void>> markLessonCompleted(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @Valid @RequestBody LessonProgressRequest request) {
        boolean isCourseCompleted = enrollmentService.markLessonCompleted(courseId, lessonId, userId, role, request);
        String message = isCourseCompleted ? "Course completed successfully." : "Lesson marked as completed.";
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(message)
                        .build()
        );
    }

    @GetMapping("/{courseId}/progress")
    @Operation(summary = "Get progress")
    public ResponseEntity<ApiResponse<Integer>> getProgress(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        Integer progress = enrollmentService.getProgress(courseId, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<Integer>builder()
                        .success(true)
                        .message("Enrollment details fetched successfully.")
                        .data(progress)
                        .build()
        );
    }

    @PatchMapping("/{courseId}/drop")
    @Operation(summary = "Drop course")
    public ResponseEntity<ApiResponse<Void>> dropCourse(
            @PathVariable String courseId,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        enrollmentService.dropCourse(courseId, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course dropped successfully.")
                        .build()
        );
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
