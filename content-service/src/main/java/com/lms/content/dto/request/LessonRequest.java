package com.lms.content.dto.request;

import com.lms.common.validation.ValidUrl;
import com.lms.content.enums.LessonContentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class LessonRequest {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Section ID is required.")
    private UUID sectionId;

    @Schema(example = "Getting Started with Spring Boot")
    @NotBlank(message = "Title is required.")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters.")
    private String title;

    @Schema(example = "Detailed guide on starting with Spring Boot.")
    private String description;

    @Schema(example = "VIDEO")
    @NotNull(message = "Content type is required.")
    private LessonContentType contentType;

    @Schema(example = "https://example.com/video.mp4")
    private String videoUrl;

    @Schema(example = "https://example.com/notes.pdf")
    private String pdfUrl;

    @Schema(example = "<p>Article content here</p>")
    private String articleContent;

    @Schema(example = "15")
    @NotNull(message = "Duration is required.")
    @Min(value = 1, message = "Zero duration rejected. Negative duration rejected.")
    private Integer durationMinutes;

    @Schema(example = "true")
    private boolean isPreview;

    @Schema(example = "1")
    @NotNull(message = "Display order is required.")
    @Min(value = 1, message = "Display order must be at least 1.")
    private Integer displayOrder;
}
