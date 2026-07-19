package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminCourseReportResponse {
    private UUID courseId;
    private long totalEnrollments;
    private long completed;
    private long dropped;
    private double averageProgress;
}
