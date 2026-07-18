package com.lms.course.dto.request;

import com.lms.common.enums.CourseLevel;
import com.lms.common.validation.ValidUrl;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "Advanced Spring Boot 3")
    @NotBlank(message = "Title is required.")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters.")
    private String title;

    @Schema(example = "Master microservices with Spring Boot.")
    @Size(max = 500, message = "Subtitle cannot exceed 500 characters.")
    private String subtitle;

    @Schema(example = "Detailed description of the course covering advanced topics in Spring Boot 3 and Spring Cloud.")
    @NotBlank(message = "Description is required.")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters.")
    private String description;

    @Schema(example = "Master Spring Boot 3.")
    @Size(max = 500, message = "Short description cannot exceed 500 characters.")
    private String shortDescription;

    @Schema(example = "https://example.com/thumbnail.png")
    @NotBlank(message = "Thumbnail URL is required.")
    @ValidUrl(message = "Invalid URLs.")
    private String thumbnailUrl;

    @Schema(example = "https://example.com/banner.png")
    @NotBlank(message = "Banner URL is required.")
    @ValidUrl(message = "Invalid URLs.")
    private String bannerUrl;

    @Schema(example = "English")
    @NotBlank(message = "Language is required.")
    private String language;

    @Schema(example = "ADVANCED")
    @NotNull(message = "Course level is required.")
    private CourseLevel courseLevel;

    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    @NotNull(message = "Category is required.")
    private UUID categoryId;

    @Schema(example = "99.99")
    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", message = "Negative prices rejected.")
    private BigDecimal price;

    @Schema(example = "49.99")
    @DecimalMin(value = "0.0", message = "Negative prices rejected.")
    private BigDecimal discountPrice;

    @Schema(example = "USD")
    @NotBlank(message = "Currency is required.")
    private String currency;

    @Schema(example = "180")
    @NotNull(message = "Duration is required.")
    @Min(value = 1, message = "Zero duration rejected. Negative duration rejected.")
    @Max(value = 100000, message = "Duration exceeds maximum.")
    private Integer estimatedDurationMinutes;
}
