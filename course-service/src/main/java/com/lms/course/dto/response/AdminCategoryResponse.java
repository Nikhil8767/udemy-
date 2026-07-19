package com.lms.course.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AdminCategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private String icon;
    private boolean isActive;
    private long courseCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
