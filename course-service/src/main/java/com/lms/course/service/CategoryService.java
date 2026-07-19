package com.lms.course.service;

import com.lms.course.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getActiveCategories();

    org.springframework.data.domain.Page<com.lms.course.dto.response.AdminCategoryResponse> getCategories(String search, int page, int size);
    com.lms.course.dto.response.AdminCategoryResponse createCategory(com.lms.course.dto.request.CategoryRequest request);
    com.lms.course.dto.response.AdminCategoryResponse updateCategory(java.util.UUID id, com.lms.course.dto.request.CategoryRequest request);
    void toggleCategoryStatus(java.util.UUID id);
    void softDeleteCategory(java.util.UUID id);
}
