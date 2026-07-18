package com.lms.content.repository;

import com.lms.content.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SectionRepository extends JpaRepository<Section, UUID> {
    List<Section> findAllByCourseIdOrderByDisplayOrderAsc(UUID courseId);
    boolean existsByCourseIdAndDisplayOrder(UUID courseId, Integer displayOrder);
    
    @Query("SELECT COALESCE(MAX(s.displayOrder), 0) FROM Section s WHERE s.courseId = :courseId")
    Integer findMaxDisplayOrderByCourseId(@Param("courseId") UUID courseId);
}
