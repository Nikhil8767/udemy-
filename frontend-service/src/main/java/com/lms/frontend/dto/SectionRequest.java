package com.lms.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SectionRequest {
    @NotBlank(message = "Course ID is required")
    private String courseId;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
