package com.lms.enrollment.dto.response;

import lombok.Data;

@Data
public class EnrollmentStatisticsResponse {
    private long totalEnrollments;
    private long completedEnrollments;
    private long activeEnrollments;
    private double averageCompletionPercentage;
}
