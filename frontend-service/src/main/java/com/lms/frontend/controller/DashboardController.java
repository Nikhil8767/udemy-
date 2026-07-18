package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.CourseServiceClient;
import com.lms.frontend.client.EnrollmentServiceClient;
import com.lms.frontend.client.UserServiceClient;
import com.lms.frontend.dto.StudentDashboardResponse;
import com.lms.frontend.dto.TutorDashboardResponse;
import com.lms.frontend.dto.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final CourseServiceClient courseServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;
    private final UserServiceClient userServiceClient;

    private void addProfileCompletion(Model model) {
        try {
            ApiResponse<UserProfileResponse> profileRes = userServiceClient.getCurrentUserProfile();
            if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                model.addAttribute("completionPercentage", profileRes.getData().getCompletionPercentage());
            } else {
                model.addAttribute("completionPercentage", 0);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch profile completion", e);
            model.addAttribute("completionPercentage", 0);
        }
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model) {
        addProfileCompletion(model);
        try {
            ApiResponse<StudentDashboardResponse> response = enrollmentServiceClient.getStudentDashboard();
            if (response != null && response.isSuccess() && response.getData() != null) {
                model.addAttribute("dashboard", response.getData());
            }
        } catch (Exception e) {
            log.error("Failed to load student dashboard statistics", e);
            model.addAttribute("errorMessage", "Failed to load dashboard statistics.");
        }
        return "student/dashboard";
    }

    @GetMapping("/tutor/dashboard")
    public String tutorDashboard(Model model) {
        addProfileCompletion(model);
        try {
            ApiResponse<TutorDashboardResponse> response = courseServiceClient.getTutorDashboard();
            if (response != null && response.isSuccess() && response.getData() != null) {
                model.addAttribute("dashboard", response.getData());
            }
        } catch (Exception e) {
            log.error("Failed to load tutor dashboard statistics", e);
            model.addAttribute("errorMessage", "Failed to load dashboard statistics.");
        }
        return "tutor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}
