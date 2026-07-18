package com.lms.course.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO used by course-service to receive Section data from content-service via Feign.
 * Fields must match what content-service SectionController returns exactly.
 */
@Data
public class SectionResponse {
    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
