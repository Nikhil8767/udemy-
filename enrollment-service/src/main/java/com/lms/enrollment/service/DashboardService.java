package com.lms.enrollment.service;

import com.lms.enrollment.dto.response.StudentDashboardResponse;

public interface DashboardService {
    StudentDashboardResponse getStudentDashboard(String userId);
}
