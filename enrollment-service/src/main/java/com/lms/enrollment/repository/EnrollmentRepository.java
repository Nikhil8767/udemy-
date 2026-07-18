package com.lms.enrollment.repository;

import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.enums.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    Optional<Enrollment> findByStudentIdAndCourseId(UUID studentId, UUID courseId);
    boolean existsByStudentIdAndCourseId(UUID studentId, UUID courseId);
    List<Enrollment> findAllByStudentId(UUID studentId);
    List<Enrollment> findAllByCourseId(UUID courseId);
    
    long countByStatus(EnrollmentStatus status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.status IN :statuses")
    long countByStatusIn(@Param("statuses") List<EnrollmentStatus> statuses);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.courseId = :courseId AND e.status IN :statuses")
    long countByCourseIdAndStatusIn(@Param("courseId") UUID courseId, @Param("statuses") List<EnrollmentStatus> statuses);

    @Query("SELECT COALESCE(AVG(e.progressPercentage), 0.0) FROM Enrollment e")
    Double getAverageCompletionPercentage();

    @Query("SELECT e FROM Enrollment e WHERE (:status IS NULL OR e.status = :status)")
    Page<Enrollment> searchEnrollments(@Param("status") EnrollmentStatus status, Pageable pageable);
}
