package com.lms.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private String status;
    private Integer progressPercentage;
    private Integer completedLessons;
    private Integer totalLessons;
    private UUID lastAccessedLessonId;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
