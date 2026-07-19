package com.lms.course.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.course.dto.request.CategoryRequest;
import com.lms.course.dto.response.AdminCategoryResponse;
import com.lms.course.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin Categories", description = "Admin endpoints for category management")
public class AdminCategoryController {

    private final CategoryService categoryService;

    private void verifyAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    @Data
    public static class AdminCategoryPaginatedResponse {
        private List<AdminCategoryResponse> content;
        private long totalElements;
        private int totalPages;
    }

    @GetMapping
    @Operation(summary = "List all categories for admin")
    public ResponseEntity<ApiResponse<AdminCategoryPaginatedResponse>> listCategories(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        verifyAdmin(roleHeader);

        Page<AdminCategoryResponse> result = categoryService.getCategories(search, page, size);
        
        AdminCategoryPaginatedResponse responseData = new AdminCategoryPaginatedResponse();
        responseData.setContent(result.getContent());
        responseData.setTotalElements(result.getTotalElements());
        responseData.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(ApiResponse.<AdminCategoryPaginatedResponse>builder()
                .success(true)
                .message("Categories fetched successfully.")
                .data(responseData)
                .build());
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> createCategory(
            @RequestHeader("X-User-Role") String roleHeader,
            @Valid @RequestBody CategoryRequest request) {
        verifyAdmin(roleHeader);
        AdminCategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(ApiResponse.<AdminCategoryResponse>builder()
                .success(true)
                .message("Category created successfully.")
                .data(response)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing category")
    public ResponseEntity<ApiResponse<AdminCategoryResponse>> updateCategory(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryRequest request) {
        verifyAdmin(roleHeader);
        AdminCategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.<AdminCategoryResponse>builder()
                .success(true)
                .message("Category updated successfully.")
                .data(response)
                .build());
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Toggle category status (Active/Inactive)")
    public ResponseEntity<ApiResponse<Void>> toggleStatus(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        categoryService.toggleCategoryStatus(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Category status toggled.")
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        categoryService.softDeleteCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted successfully.")
                .build());
    }
}
