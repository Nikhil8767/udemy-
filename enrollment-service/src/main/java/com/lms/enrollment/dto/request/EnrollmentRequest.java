package com.lms.enrollment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class EnrollmentRequest {
    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Course ID is required.")
    private UUID courseId;
}
