package com.lms.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class SectionRequest {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Course ID is required.")
    private UUID courseId;

    @Schema(example = "Introduction to Spring Boot")
    @NotBlank(message = "Title is required.")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters.")
    private String title;

    @Schema(example = "Learn the basics of Spring Boot.")
    private String description;

    @Schema(example = "1")
    @NotNull(message = "Display order is required.")
    @Min(value = 1, message = "Display order must be at least 1.")
    private Integer displayOrder;
}
