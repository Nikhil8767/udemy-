package com.lms.course.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.course.dto.response.TutorDashboardResponse;
import com.lms.course.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/courses/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for dashboard statistics")
public class TutorDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/tutor")
    @Operation(summary = "Get Tutor Dashboard Statistics")
    public ResponseEntity<ApiResponse<TutorDashboardResponse>> getTutorDashboard(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(
                ApiResponse.<TutorDashboardResponse>builder()
                        .success(true)
                        .message("Dashboard statistics fetched successfully.")
                        .data(dashboardService.getTutorDashboard(userId))
                        .build()
        );
    }
}
