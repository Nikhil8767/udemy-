package com.lms.enrollment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lesson_progress", uniqueConstraints = @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(nullable = false)
    private UUID lessonId;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer watchTimeMinutes = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer lastPositionSeconds = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
