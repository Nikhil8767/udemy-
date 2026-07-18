package com.lms.content.dto.request;

import com.lms.common.validation.ValidUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ResourceRequest {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Lesson ID is required.")
    private UUID lessonId;

    @Schema(example = "Source Code")
    @NotBlank(message = "Title is required.")
    private String title;

    @Schema(example = "PDF")
    private String resourceType;

    @Schema(example = "https://example.com/source.zip")
    @NotBlank(message = "File URL is required.")
    private String fileUrl;
}
