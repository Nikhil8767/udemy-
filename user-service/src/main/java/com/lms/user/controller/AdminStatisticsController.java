package com.lms.user.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.user.client.AuthServiceClient;
import com.lms.user.dto.response.AuthStatisticsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users/statistics")
@RequiredArgsConstructor
@Tag(name = "Admin User Statistics", description = "Admin endpoints for user statistics")
public class AdminStatisticsController {
    
    private final AuthServiceClient authServiceClient;
    
    @GetMapping
    @Operation(summary = "User Statistics")
    public ResponseEntity<ApiResponse<AuthStatisticsResponse>> getStatistics(
            @RequestHeader("X-User-Role") String roleHeader) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(roleHeader)) {
            throw new UnauthorizedException("Access denied.");
        }
        return ResponseEntity.ok(ApiResponse.<AuthStatisticsResponse>builder()
                .success(true)
                .message("Statistics fetched successfully.")
                .data(authServiceClient.getAuthStatistics())
                .build());
    }
}
