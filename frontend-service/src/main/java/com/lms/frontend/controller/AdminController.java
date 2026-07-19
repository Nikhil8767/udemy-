package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.AdminCourseServiceClient;
import com.lms.frontend.client.AdminEnrollmentServiceClient;
import com.lms.frontend.client.AdminUserServiceClient;
import com.lms.frontend.dto.admin.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminUserServiceClient adminUserServiceClient;
    private final AdminCourseServiceClient adminCourseServiceClient;
    private final AdminEnrollmentServiceClient adminEnrollmentServiceClient;
    private final com.lms.frontend.client.AdminCategoryServiceClient adminCategoryServiceClient;
    private final com.lms.frontend.client.AdminSettingsServiceClient adminSettingsServiceClient;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeMenu", "dashboard");
        AdminDashboardDTO dashboardDTO = new AdminDashboardDTO();
        try {
            ApiResponse<AuthStatisticsResponse> userStats = adminUserServiceClient.getStatistics();
            if (userStats != null && userStats.isSuccess()) {
                dashboardDTO.setUserStats(userStats.getData());
            }
            
            ApiResponse<CourseStatisticsResponse> courseStats = adminCourseServiceClient.getStatistics();
            if (courseStats != null && courseStats.isSuccess()) {
                dashboardDTO.setCourseStats(courseStats.getData());
            }
        } catch (Exception e) {
            log.error("Failed to load admin dashboard statistics", e);
            model.addAttribute("errorMessage", "Failed to load complete dashboard statistics.");
        }
        
        model.addAttribute("dashboard", dashboardDTO);
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("activeMenu", "users");
        try {
            ApiResponse<AdminUserPaginatedResponse> response = adminUserServiceClient.listUsers(role, status, page, 10);
            if (response != null && response.isSuccess()) {
                model.addAttribute("usersPage", response.getData());
                model.addAttribute("currentPage", page);
            }
        } catch (Exception e) {
            log.error("Failed to fetch users", e);
            model.addAttribute("errorMessage", "Unable to fetch users.");
        }
        return "admin/users";
    }
    
    @PostMapping("/users/{id}/activate")
    public String activateUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            adminUserServiceClient.activateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User activated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to activate user.");
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/deactivate")
    public String deactivateUser(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            adminUserServiceClient.deactivateUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deactivated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to deactivate user.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/courses")
    public String courses(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("activeMenu", "courses");
        try {
            ApiResponse<AdminCoursePaginatedResponse> response = adminCourseServiceClient.listCourses(status, null, null, page, 10);
            if (response != null && response.isSuccess()) {
                model.addAttribute("coursesPage", response.getData());
                model.addAttribute("currentPage", page);
            }
        } catch (Exception e) {
            log.error("Failed to fetch courses", e);
            model.addAttribute("errorMessage", "Unable to fetch courses.");
        }
        return "admin/courses";
    }

    @PostMapping("/courses/{id}/publish")
    public String publishCourse(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            adminCourseServiceClient.publishCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course published.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to publish course.");
        }
        return "redirect:/admin/courses";
    }

    @PostMapping("/courses/{id}/archive")
    public String archiveCourse(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            adminCourseServiceClient.archiveCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course archived.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to archive course.");
        }
        return "redirect:/admin/courses";
    }

    @GetMapping("/enrollments")
    public String enrollments(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("activeMenu", "enrollments");
        try {
            ApiResponse<AdminEnrollmentPaginatedResponse> response = adminEnrollmentServiceClient.listEnrollments(status, page, 10);
            if (response != null && response.isSuccess()) {
                model.addAttribute("enrollmentsPage", response.getData());
                model.addAttribute("currentPage", page);
            }
        } catch (Exception e) {
            log.error("Failed to fetch enrollments", e);
            model.addAttribute("errorMessage", "Unable to fetch enrollments.");
        }
        return "admin/enrollments";
    }

    @GetMapping("/users/{id}")
    public String userDetails(@PathVariable UUID id, Model model) {
        model.addAttribute("activeMenu", "users");
        return "admin/user-details";
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable UUID id, Model model) {
        model.addAttribute("activeMenu", "courses");
        return "admin/course-details";
    }

    @GetMapping("/categories")
    public String categories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("activeMenu", "categories");
        try {
            ApiResponse<java.util.Map<String, Object>> response = adminCategoryServiceClient.listCategories(search, page, 10);
            if (response != null && response.isSuccess()) {
                model.addAttribute("categoriesPage", response.getData());
                model.addAttribute("currentPage", page);
            }
        } catch (Exception e) {
            log.error("Failed to fetch categories", e);
            model.addAttribute("errorMessage", "Unable to fetch categories.");
        }
        return "admin/categories";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("activeMenu", "reports");
        return "admin/reports";
    }

    @GetMapping("/analytics")
    public String analytics(Model model) {
        model.addAttribute("activeMenu", "analytics");
        return "admin/analytics";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("activeMenu", "profile");
        return "admin/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activeMenu", "settings");
        try {
            ApiResponse<java.util.Map<String, String>> response = adminSettingsServiceClient.getSettings();
            if (response != null && response.isSuccess()) {
                model.addAttribute("settings", response.getData());
            }
        } catch (Exception e) {
            log.error("Failed to fetch settings", e);
            model.addAttribute("errorMessage", "Unable to fetch settings.");
        }
        return "admin/settings";
    }

    @PostMapping("/settings/update")
    public String updateSettings(@RequestParam java.util.Map<String, String> allParams, RedirectAttributes redirectAttributes) {
        try {
            java.util.Map<String, Object> request = new java.util.HashMap<>();
            java.util.Map<String, String> settingsMap = new java.util.HashMap<>();
            
            // Extract parameters that start with settings[
            allParams.forEach((key, value) -> {
                if (key.startsWith("settings[") && key.endsWith("]")) {
                    String settingKey = key.substring(9, key.length() - 1);
                    // Remove quotes if present
                    if (settingKey.startsWith("'") && settingKey.endsWith("'")) {
                        settingKey = settingKey.substring(1, settingKey.length() - 1);
                    }
                    settingsMap.put(settingKey, value);
                }
            });
            
            // For unchecked checkboxes which don't send values
            String[] booleanKeys = {"maintenance_mode", "student_registration_enabled", "tutor_registration_enabled", "course_approval_required"};
            for (String key : booleanKeys) {
                if (!settingsMap.containsKey(key)) {
                    settingsMap.put(key, "false");
                }
            }

            request.put("settings", settingsMap);
            adminSettingsServiceClient.updateSettings(request);
            redirectAttributes.addFlashAttribute("successMessage", "Settings updated successfully.");
        } catch (Exception e) {
            log.error("Failed to update settings", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update settings.");
        }
        return "redirect:/admin/settings";
    }
}
