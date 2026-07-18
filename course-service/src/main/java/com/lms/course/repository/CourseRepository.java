package com.lms.course.repository;

import com.lms.common.enums.CourseStatus;
import com.lms.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByTitle(String title);
    List<Course> findAllByCourseStatus(CourseStatus status);
    List<Course> findAllByInstructorId(UUID instructorId);
    
    long countByCourseStatus(CourseStatus status);
    long countByIsFeaturedTrue();

    @Query("SELECT c FROM Course c WHERE (:status IS NULL OR c.courseStatus = :status) AND (:categoryId IS NULL OR CAST(c.category.id AS string) = CAST(:categoryId AS string)) AND (:instructorId IS NULL OR CAST(c.instructorId AS string) = CAST(:instructorId AS string))")
    Page<Course> searchCourses(@Param("status") CourseStatus status, @Param("categoryId") UUID categoryId, @Param("instructorId") UUID instructorId, Pageable pageable);
}
