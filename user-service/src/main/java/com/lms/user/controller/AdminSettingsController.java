package com.lms.user.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.user.dto.request.SettingsUpdateRequest;
import com.lms.user.service.SystemSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
@Tag(name = "Admin Settings", description = "Admin endpoints for platform settings")
public class AdminSettingsController {

    private final SystemSettingService settingService;

    private void verifyAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    @GetMapping
    @Operation(summary = "Get all platform settings")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSettings(
            @RequestHeader("X-User-Role") String roleHeader) {
        verifyAdmin(roleHeader);
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Settings fetched successfully.")
                .data(settingService.getAllSettings())
                .build());
    }

    @PutMapping
    @Operation(summary = "Update platform settings")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateSettings(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestBody SettingsUpdateRequest request) {
        verifyAdmin(roleHeader);
        Map<String, String> updated = settingService.updateSettings(request);
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .success(true)
                .message("Settings updated successfully.")
                .data(updated)
                .build());
    }
}
