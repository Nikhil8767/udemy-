package com.lms.course.service;

import com.lms.course.dto.response.TutorDashboardResponse;

public interface DashboardService {
    TutorDashboardResponse getTutorDashboard(String userId);
}
