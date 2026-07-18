package com.lms.content.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ResourceResponse {
    private UUID id;
    private UUID lessonId;
    private String title;
    private String resourceType;
    private String fileUrl;
    private LocalDateTime createdAt;
}
