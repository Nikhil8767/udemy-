package com.lms.enrollment.dto.response;

import com.lms.enrollment.enums.EnrollmentStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class EnrollmentResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private EnrollmentStatus status;
    private Integer progressPercentage;
    private Integer completedLessons;
    private Integer totalLessons;
    private UUID lastAccessedLessonId;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
