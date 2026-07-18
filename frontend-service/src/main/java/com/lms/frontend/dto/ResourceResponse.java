package com.lms.frontend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResourceResponse {
    private String id;
    private String lessonId;
    private String title;
    private String resourceType;
    private String fileUrl;
    private LocalDateTime createdAt;
}
