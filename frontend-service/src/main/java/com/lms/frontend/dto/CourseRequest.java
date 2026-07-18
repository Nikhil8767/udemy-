package com.lms.frontend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CourseRequest {
    @NotBlank(message = "Title is required.")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters.")
    private String title;

    @Size(max = 500, message = "Subtitle cannot exceed 500 characters.")
    private String subtitle;

    @NotBlank(message = "Description is required.")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters.")
    private String description;

    @Size(max = 500, message = "Short description cannot exceed 500 characters.")
    private String shortDescription;

    @NotBlank(message = "Thumbnail URL is required.")
    private String thumbnailUrl;

    @NotBlank(message = "Banner URL is required.")
    private String bannerUrl;

    @NotBlank(message = "Language is required.")
    private String language;

    @NotNull(message = "Course level is required.")
    private String courseLevel; // Using String to avoid pulling Enum from common if not available, though CourseLevel is in common-library

    @NotNull(message = "Category is required.")
    private UUID categoryId;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", message = "Negative prices rejected.")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Negative prices rejected.")
    private BigDecimal discountPrice;

    @NotBlank(message = "Currency is required.")
    private String currency;

    @NotNull(message = "Duration is required.")
    @Min(value = 1, message = "Zero duration rejected. Negative duration rejected.")
    @Max(value = 100000, message = "Duration exceeds maximum.")
    private Integer estimatedDurationMinutes;
}
