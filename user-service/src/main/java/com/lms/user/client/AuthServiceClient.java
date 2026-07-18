package com.lms.user.client;

import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import com.lms.user.dto.response.AuthStatisticsResponse;
import com.lms.user.dto.response.InternalUserSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {

    @GetMapping("/internal/users/search")
    InternalUserSearchResponse searchUsers(
            @RequestParam(value = "role", required = false) Role role,
            @RequestParam(value = "status", required = false) AccountStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @PatchMapping("/internal/users/{id}/status")
    void updateStatus(@PathVariable("id") UUID id, @RequestParam("status") AccountStatus status);

    @PatchMapping("/internal/users/{id}/role")
    void updateRole(@PathVariable("id") UUID id, @RequestParam("role") Role role);

    @GetMapping("/internal/statistics")
    AuthStatisticsResponse getAuthStatistics();
}
