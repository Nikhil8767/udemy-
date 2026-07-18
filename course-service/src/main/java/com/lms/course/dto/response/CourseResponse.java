package com.lms.course.dto.response;

import com.lms.common.enums.CourseLevel;
import com.lms.common.enums.CourseStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CourseResponse {
    private UUID id;
    private String title;
    private String subtitle;
    private String description;
    private String shortDescription;
    private String thumbnailUrl;
    private String bannerUrl;
    private String language;
    private CourseLevel courseLevel;
    private CourseStatus courseStatus;
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
}
