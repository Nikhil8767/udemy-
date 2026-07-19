package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "api-gateway", contextId = "adminSettingsServiceClient", path = "/api/v1/admin/settings")
public interface AdminSettingsServiceClient {

    @GetMapping
    ApiResponse<Map<String, String>> getSettings();

    @PutMapping
    ApiResponse<Map<String, String>> updateSettings(
            @RequestBody Map<String, Object> request);
}
