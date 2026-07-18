package com.lms.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String description;
    private String contentType; // VIDEO, PDF, ARTICLE
    private String videoUrl;
    private String pdfUrl;
    private String articleContent;
    private Integer durationMinutes;
    private boolean isPreview;
    private Integer displayOrder;
    private List<ResourceResponse> resources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
