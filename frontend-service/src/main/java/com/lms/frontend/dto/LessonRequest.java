package com.lms.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank(message = "Section ID is required")
    private String sectionId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Content type is required")
    private String contentType;

    private String videoUrl;
    
    private String pdfUrl;
    
    private String articleContent;

    private Integer durationMinutes;

    private boolean isPreview;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
