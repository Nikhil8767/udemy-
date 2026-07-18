package com.lms.course.service.impl;

import com.lms.course.dto.response.CategoryResponse;
import com.lms.course.repository.CategoryRepository;
import com.lms.course.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(cat -> CategoryResponse.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .description(cat.getDescription())
                        .icon(cat.getIcon())
                        .isActive(cat.isActive())
                        .build())
                .collect(Collectors.toList());
    }
}
