package com.lms.enrollment.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.enrollment.dto.response.EnrollmentStatisticsResponse;
import com.lms.enrollment.enums.EnrollmentStatus;
import com.lms.enrollment.repository.EnrollmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/admin/enrollments/statistics")
@RequiredArgsConstructor
@Tag(name = "Admin Enrollment Statistics", description = "Admin endpoints for enrollment statistics")
public class AdminStatisticsController {
    
    private final EnrollmentRepository enrollmentRepository;
    
    @GetMapping
    @Operation(summary = "Enrollment Statistics")
    public ResponseEntity<ApiResponse<EnrollmentStatisticsResponse>> getStatistics(
            @RequestHeader("X-User-Role") String roleHeader) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(roleHeader)) {
            throw new UnauthorizedException("Access denied.");
        }
        EnrollmentStatisticsResponse stats = new EnrollmentStatisticsResponse();
        stats.setTotalEnrollments(enrollmentRepository.count());
        stats.setCompletedEnrollments(enrollmentRepository.countByStatus(EnrollmentStatus.COMPLETED));
        stats.setActiveEnrollments(enrollmentRepository.countByStatusIn(Arrays.asList(EnrollmentStatus.ENROLLED, EnrollmentStatus.IN_PROGRESS)));
        stats.setAverageCompletionPercentage(enrollmentRepository.getAverageCompletionPercentage());
        
        return ResponseEntity.ok(ApiResponse.<EnrollmentStatisticsResponse>builder()
                .success(true)
                .message("Statistics fetched successfully.")
                .data(stats)
                .build());
    }
}
