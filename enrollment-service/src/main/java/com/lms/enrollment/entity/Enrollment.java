package com.lms.enrollment.entity;

import com.lms.enrollment.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "enrollments", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID studentId;

    @Column(nullable = false)
    private UUID courseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;

    @Column(nullable = false)
    @Builder.Default
    private Integer progressPercentage = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer completedLessons = 0;

    @Column(nullable = false)
    private Integer totalLessons;

    private UUID lastAccessedLessonId;
    
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime enrolledAt = LocalDateTime.now();

    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LessonProgress> lessonProgresses = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
