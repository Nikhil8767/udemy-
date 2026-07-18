package com.lms.course.service;

import com.lms.course.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getActiveCategories();
}
