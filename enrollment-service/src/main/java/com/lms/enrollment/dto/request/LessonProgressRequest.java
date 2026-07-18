package com.lms.enrollment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonProgressRequest {
    @Schema(example = "15")
    @NotNull(message = "Watch Time is required.")
    @Min(value = 0, message = "Negative values rejected.")
    private Integer watchTimeMinutes;

    @Schema(example = "900")
    @NotNull(message = "Last Position is required.")
    @Min(value = 0, message = "Negative values rejected.")
    private Integer lastPositionSeconds;
}
