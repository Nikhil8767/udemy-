package com.lms.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResourceRequest {
    @NotBlank(message = "Lesson ID is required")
    private String lessonId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Resource type is required")
    private String resourceType;

    @NotBlank(message = "File URL is required")
    private String fileUrl;
}
