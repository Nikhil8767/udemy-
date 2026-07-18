package com.lms.course.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;
    private String icon;
    private boolean isActive;
}
