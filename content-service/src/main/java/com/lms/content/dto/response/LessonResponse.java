package com.lms.content.dto.response;

import com.lms.content.enums.LessonContentType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LessonResponse {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String description;
    private LessonContentType contentType;
    private String videoUrl;
    private String pdfUrl;
    private String articleContent;
    private Integer durationMinutes;
    private boolean isPreview;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
