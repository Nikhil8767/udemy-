package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "api-gateway", contextId = "adminUserServiceClient", path = "/api/v1/admin/users")
public interface AdminUserServiceClient {

    @GetMapping("/statistics")
    ApiResponse<com.lms.frontend.dto.admin.AuthStatisticsResponse> getStatistics();

    @GetMapping
    ApiResponse<com.lms.frontend.dto.admin.AdminUserPaginatedResponse> listUsers(
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @GetMapping("/{id}")
    ApiResponse<com.lms.frontend.dto.admin.AdminUserDetailsResponse> getUserDetails(@PathVariable("id") UUID id);

    @PatchMapping("/{id}/activate")
    ApiResponse<Void> activateUser(@PathVariable("id") UUID id);

    @PatchMapping("/{id}/deactivate")
    ApiResponse<Void> deactivateUser(@PathVariable("id") UUID id);

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteUser(@PathVariable("id") UUID id);
}
