package com.lms.frontend.dto.admin;

import lombok.Data;

@Data
public class AuthStatisticsResponse {
    private long totalUsers;
    private long totalStudents;
    private long totalTutors;
    private long pendingTutorRequests;
}
