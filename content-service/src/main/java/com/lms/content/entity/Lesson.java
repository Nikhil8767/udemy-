package com.lms.content.entity;

import com.lms.content.enums.LessonContentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lessons", uniqueConstraints = @UniqueConstraint(columnNames = {"section_id", "display_order"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonContentType contentType;

    private String videoUrl;
    private String pdfUrl;
    
    @Column(columnDefinition = "TEXT")
    private String articleContent;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    @Builder.Default
    private boolean isPreview = false;

    @Column(nullable = false)
    private Integer displayOrder;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Resource> resources = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
