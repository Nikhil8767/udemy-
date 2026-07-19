package com.lms.frontend.dto.admin;

import lombok.Data;

@Data
public class AdminDashboardDTO {
    private AuthStatisticsResponse userStats = new AuthStatisticsResponse();
    private CourseStatisticsResponse courseStats = new CourseStatisticsResponse();
}
