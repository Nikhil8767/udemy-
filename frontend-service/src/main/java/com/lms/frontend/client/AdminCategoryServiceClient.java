package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "api-gateway", contextId = "adminCategoryServiceClient", path = "/api/v1/admin/categories")
public interface AdminCategoryServiceClient {

    @GetMapping
    ApiResponse<Map<String, Object>> listCategories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @PostMapping
    ApiResponse<Map<String, Object>> createCategory(
            @RequestBody Map<String, Object> request);

    @PutMapping("/{id}")
    ApiResponse<Map<String, Object>> updateCategory(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request);

    @PatchMapping("/{id}/toggle-status")
    ApiResponse<Void> toggleStatus(
            @PathVariable UUID id);

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteCategory(
            @PathVariable UUID id);
}
