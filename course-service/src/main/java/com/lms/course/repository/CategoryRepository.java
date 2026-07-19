package com.lms.course.repository;

import com.lms.course.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByName(String name);
    java.util.List<Category> findByIsActiveTrueAndIsDeletedFalse();
    org.springframework.data.domain.Page<Category> findByIsDeletedFalse(org.springframework.data.domain.Pageable pageable);
    org.springframework.data.domain.Page<Category> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, org.springframework.data.domain.Pageable pageable);
}
