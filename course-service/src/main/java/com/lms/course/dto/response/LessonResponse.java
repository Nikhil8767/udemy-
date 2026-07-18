package com.lms.course.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO used by course-service to receive Lesson data from content-service via Feign.
 * Fields must match what content-service LessonController returns exactly.
 * contentType is kept as String (Jackson will deserialize enum to its String name).
 */
@Data
public class LessonResponse {
    private UUID id;
    private UUID sectionId;
    private String title;
    private String description;
    private String contentType;
    private String videoUrl;
    private String pdfUrl;
    private String articleContent;
    private Integer durationMinutes;
    private boolean isPreview;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
