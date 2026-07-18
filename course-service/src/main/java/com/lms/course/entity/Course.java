package com.lms.course.entity;

import com.lms.common.enums.CourseLevel;
import com.lms.common.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String title;

    private String subtitle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    private String shortDescription;
    private String thumbnailUrl;
    private String bannerUrl;
    private String language;

    @Enumerated(EnumType.STRING)
    private CourseLevel courseLevel;

    @Enumerated(EnumType.STRING)
    private CourseStatus courseStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private BigDecimal price;
    private BigDecimal discountPrice;
    private String currency;
    
    @Column(nullable = false)
    private Integer estimatedDurationMinutes;

    @Column(nullable = false)
    private UUID instructorId;

    @Column(nullable = false)
    private UUID createdBy;

    private UUID updatedBy;

    @Column(nullable = false)
    @Builder.Default
    private boolean isFeatured = false;

    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
