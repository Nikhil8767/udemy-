package com.lms.content.repository;

import com.lms.content.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {
    List<Lesson> findAllBySectionIdOrderByDisplayOrderAsc(UUID sectionId);
    boolean existsBySectionIdAndDisplayOrder(UUID sectionId, Integer displayOrder);
    
    @Query("SELECT COALESCE(MAX(l.displayOrder), 0) FROM Lesson l WHERE l.section.id = :sectionId")
    Integer findMaxDisplayOrderBySectionId(@Param("sectionId") UUID sectionId);
}
