package com.lms.enrollment.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.enrollment.dto.response.StudentDashboardResponse;
import com.lms.enrollment.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/enrollments/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for dashboard statistics")
public class StudentDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @Operation(summary = "Get Student Dashboard Statistics")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(
                ApiResponse.<StudentDashboardResponse>builder()
                        .success(true)
                        .message("Dashboard statistics fetched successfully.")
                        .data(dashboardService.getStudentDashboard(userId))
                        .build()
        );
    }
}
