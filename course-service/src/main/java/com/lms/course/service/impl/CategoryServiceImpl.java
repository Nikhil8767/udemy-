package com.lms.course.service.impl;

import com.lms.common.exception.ResourceNotFoundException;
import com.lms.course.dto.request.CategoryRequest;
import com.lms.course.dto.response.AdminCategoryResponse;
import com.lms.course.dto.response.CategoryResponse;
import com.lms.course.entity.Category;
import com.lms.course.repository.CategoryRepository;
import com.lms.course.repository.CourseRepository;
import com.lms.course.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    @Override
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueAndIsDeletedFalse().stream()
                .map(cat -> CategoryResponse.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .icon(cat.getIcon())
                        .isActive(cat.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<AdminCategoryResponse> getCategories(String search, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Category> categoryPage;
        
        if (search != null && !search.trim().isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(search.trim(), pageRequest);
        } else {
            categoryPage = categoryRepository.findByIsDeletedFalse(pageRequest);
        }

        return categoryPage.map(cat -> AdminCategoryResponse.builder()
                .id(cat.getId())
                .name(cat.getName())
                .description(cat.getDescription())
                .icon(cat.getIcon())
                .isActive(cat.isActive())
                .courseCount(courseRepository.countByCategory_Id(cat.getId()))
                .createdAt(cat.getCreatedAt())
                .updatedAt(cat.getUpdatedAt())
                .build());
    }

    @Override
    public AdminCategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .isActive(true)
                .isDeleted(false)
                .build();

        Category saved = categoryRepository.save(category);
        return mapToAdminResponse(saved);
    }

    @Override
    public AdminCategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());

        Category saved = categoryRepository.save(category);
        return mapToAdminResponse(saved);
    }

    @Override
    public void toggleCategoryStatus(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setActive(!category.isActive());
        categoryRepository.save(category);
    }

    @Override
    public void softDeleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        long courseCount = courseRepository.countByCategory_Id(id);
        if (courseCount > 0) {
            throw new IllegalStateException("Cannot delete category with associated courses");
        }
        
        category.setDeleted(true);
        category.setActive(false);
        categoryRepository.save(category);
    }

    private AdminCategoryResponse mapToAdminResponse(Category cat) {
        return AdminCategoryResponse.builder()
                .id(cat.getId())
                .name(cat.getName())
                .description(cat.getDescription())
                .icon(cat.getIcon())
                .isActive(cat.isActive())
                .courseCount(courseRepository.countByCategory_Id(cat.getId()))
                .createdAt(cat.getCreatedAt())
                .updatedAt(cat.getUpdatedAt())
                .build();
    }
}
