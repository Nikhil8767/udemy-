package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.CourseServiceClient;
import com.lms.frontend.dto.CourseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final CourseServiceClient courseServiceClient;

    @GetMapping("/student/dashboard")
    public String studentDashboard() {
        return "student/dashboard";
    }

    @GetMapping("/tutor/dashboard")
    public String tutorDashboard(Model model) {
        int totalCourses = 0;
        int publishedCourses = 0;
        int draftCourses = 0;
        int archivedCourses = 0;

        try {
            ApiResponse<List<CourseResponse>> response = courseServiceClient.getMyCourses();
            if (response != null && response.isSuccess() && response.getData() != null) {
                List<CourseResponse> courses = response.getData();
                totalCourses = courses.size();
                for (CourseResponse course : courses) {
                    if ("PUBLISHED".equalsIgnoreCase(course.getCourseStatus())) {
                        publishedCourses++;
                    } else if ("DRAFT".equalsIgnoreCase(course.getCourseStatus())) {
                        draftCourses++;
                    } else if ("ARCHIVED".equalsIgnoreCase(course.getCourseStatus())) {
                        archivedCourses++;
                    }
                }
                model.addAttribute("recentCourses", courses.size() > 5 ? courses.subList(0, 5) : courses);
            }
        } catch (Exception e) {
            log.error("Failed to load course statistics for tutor dashboard", e);
            model.addAttribute("errorMessage", "Failed to load latest statistics. Please try again later.");
        }

        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("publishedCourses", publishedCourses);
        model.addAttribute("draftCourses", draftCourses);
        model.addAttribute("archivedCourses", archivedCourses);

        return "tutor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }
}
