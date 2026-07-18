package com.lms.enrollment.dto.response;

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
public class EnrollmentStatusDTO {
    private boolean enrolled;
    private UUID enrollmentId;
    private Integer progressPercentage;
    private UUID lastAccessedLessonId;
    private Integer completedLessons;
    private LocalDateTime enrollmentDate;
}
