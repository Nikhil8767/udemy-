package com.lms.enrollment.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.enums.EnrollmentStatus;
import com.lms.enrollment.repository.EnrollmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Enrollments", description = "Admin endpoints for enrollment reports")
public class AdminEnrollmentController {

    private final EnrollmentRepository enrollmentRepository;

    private void verifyAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    @Data
    public static class AdminEnrollmentResponse {
        private UUID id;
        private UUID studentId;
        private UUID courseId;
        private EnrollmentStatus status;
        private Integer progressPercentage;
    }

    @Data
    public static class AdminPaginatedResponse<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;
    }

    @Data
    public static class AdminCourseReportResponse {
        private UUID courseId;
        private long totalEnrollments;
        private long completed;
        private long dropped;
        private double averageProgress;
    }

    @Data
    public static class AdminStudentReportResponse {
        private UUID studentId;
        private long totalEnrolled;
        private long completed;
        private long dropped;
    }

    @GetMapping("/enrollments")
    @Operation(summary = "List all enrollments")
    public ResponseEntity<ApiResponse<AdminPaginatedResponse<AdminEnrollmentResponse>>> listEnrollments(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestParam(required = false) EnrollmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        verifyAdmin(roleHeader);

        Page<Enrollment> result = enrollmentRepository.searchEnrollments(
                status, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        
        List<AdminEnrollmentResponse> content = result.getContent().stream().map(enrollment -> {
            AdminEnrollmentResponse dto = new AdminEnrollmentResponse();
            dto.setId(enrollment.getId());
            dto.setStudentId(enrollment.getStudentId());
            dto.setCourseId(enrollment.getCourseId());
            dto.setStatus(enrollment.getStatus());
            dto.setProgressPercentage(enrollment.getProgressPercentage());
            return dto;
        }).collect(Collectors.toList());

        AdminPaginatedResponse<AdminEnrollmentResponse> responseData = new AdminPaginatedResponse<>();
        responseData.setContent(content);
        responseData.setTotalElements(result.getTotalElements());
        responseData.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(ApiResponse.<AdminPaginatedResponse<AdminEnrollmentResponse>>builder()
                .success(true)
                .message("Enrollments fetched successfully.")
                .data(responseData)
                .build());
    }

    @GetMapping("/reports/course/{courseId}")
    @Operation(summary = "Course Report")
    public ResponseEntity<ApiResponse<AdminCourseReportResponse>> courseReport(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID courseId) {
        verifyAdmin(roleHeader);
        
        List<Enrollment> enrollments = enrollmentRepository.findAllByCourseId(courseId);
        
        AdminCourseReportResponse report = new AdminCourseReportResponse();
        report.setCourseId(courseId);
        report.setTotalEnrollments(enrollments.size());
        report.setCompleted(enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count());
        report.setDropped(enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.DROPPED).count());
        
        double avg = enrollments.stream().mapToInt(Enrollment::getProgressPercentage).average().orElse(0.0);
        report.setAverageProgress(avg);

        return ResponseEntity.ok(ApiResponse.<AdminCourseReportResponse>builder()
                .success(true)
                .message("Report fetched successfully.")
                .data(report)
                .build());
    }

    @GetMapping("/reports/student/{studentId}")
    @Operation(summary = "Student Report")
    public ResponseEntity<ApiResponse<AdminStudentReportResponse>> studentReport(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID studentId) {
        verifyAdmin(roleHeader);
        
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(studentId);
        
        AdminStudentReportResponse report = new AdminStudentReportResponse();
        report.setStudentId(studentId);
        report.setTotalEnrolled(enrollments.size());
        report.setCompleted(enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.COMPLETED).count());
        report.setDropped(enrollments.stream().filter(e -> e.getStatus() == EnrollmentStatus.DROPPED).count());

        return ResponseEntity.ok(ApiResponse.<AdminStudentReportResponse>builder()
                .success(true)
                .message("Report fetched successfully.")
                .data(report)
                .build());
    }
}
