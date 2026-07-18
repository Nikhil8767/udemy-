package com.lms.course.dto.response;

import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class ResourceResponse {
    private UUID id;
    private UUID lessonId;
    private String title;
    private String resourceType;
    private String fileUrl;
    private LocalDateTime createdAt;
}
