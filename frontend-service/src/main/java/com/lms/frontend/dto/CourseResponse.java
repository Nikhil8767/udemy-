package com.lms.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private UUID id;
    private String title;
    private String subtitle;
    private String description;
    private String shortDescription;
    private String thumbnailUrl;
    private String bannerUrl;
    private String language;
    private String courseLevel;
    private String courseStatus;
    private UUID categoryId;
    private String categoryName;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String currency;
    private Integer estimatedDurationMinutes;
    private UUID instructorId;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long studentCount;
}
