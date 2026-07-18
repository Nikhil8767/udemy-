package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.CourseServiceClient;
import com.lms.frontend.client.EnrollmentServiceClient;
import com.lms.frontend.dto.CourseResponse;
import com.lms.frontend.dto.EnrollmentRequest;
import com.lms.frontend.dto.EnrollmentResponse;
import com.lms.frontend.dto.LessonProgressRequest;
import com.lms.frontend.dto.MyCourseDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final CourseServiceClient courseServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;

    @GetMapping("/courses")
    public String browseCourses(Model model) {
        try {
            ApiResponse<List<CourseResponse>> response = courseServiceClient.getPublishedCourses();
            if (response != null && response.isSuccess()) {
                model.addAttribute("courses", response.getData());
            }
        } catch (Exception e) {
            log.error("Error fetching courses", e);
            model.addAttribute("errorMessage", "Failed to load courses. Please try again later.");
        }
        return "student/courses";
    }

    @GetMapping("/my-courses")
    public String myCourses(Model model) {
        try {
            ApiResponse<List<EnrollmentResponse>> enrollmentsResponse = enrollmentServiceClient.getMyEnrollments();
            List<MyCourseDTO> myCourses = new ArrayList<>();
            
            if (enrollmentsResponse != null && enrollmentsResponse.isSuccess() && enrollmentsResponse.getData() != null) {
                for (EnrollmentResponse enrollment : enrollmentsResponse.getData()) {
                    try {
                        ApiResponse<CourseResponse> courseRes = courseServiceClient.getCourseDetails(enrollment.getCourseId().toString());
                        if (courseRes != null && courseRes.isSuccess() && courseRes.getData() != null) {
                            myCourses.add(new MyCourseDTO(courseRes.getData(), enrollment));
                        }
                    } catch (Exception ex) {
                        log.warn("Failed to fetch course details for courseId: {}", enrollment.getCourseId());
                    }
                }
            }
            model.addAttribute("myCourses", myCourses);
        } catch (Exception e) {
            log.error("Error fetching my courses", e);
            model.addAttribute("errorMessage", "Failed to load enrolled courses.");
        }
        return "student/my-courses";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam String courseId, RedirectAttributes redirectAttributes) {
        try {
            EnrollmentRequest request = new EnrollmentRequest(UUID.fromString(courseId));
            ApiResponse<Void> response = enrollmentServiceClient.enroll(request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Successfully enrolled in the course!");
                return "redirect:/student/my-courses";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to enroll. " + (response != null ? response.getMessage() : ""));
            }
        } catch (FeignException.Conflict e) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are already enrolled in this course.");
        } catch (Exception e) {
            log.error("Error enrolling in course", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/courses/" + courseId;
    }

    @PostMapping("/enrollments/{courseId}/complete-lesson/{lessonId}")
    public String markLessonCompleted(
            @PathVariable String courseId,
            @PathVariable String lessonId,
            RedirectAttributes redirectAttributes) {
        try {
            LessonProgressRequest request = new LessonProgressRequest(true);
            ApiResponse<Void> response = enrollmentServiceClient.markLessonCompleted(courseId, lessonId, request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", response.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to mark lesson as completed.");
            }
        } catch (Exception e) {
            log.error("Error marking lesson as completed", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/courses/" + courseId + "/learn?lessonId=" + lessonId;
    }

    @GetMapping("/certificates")
    public String getCertificates(Model model) {
        // Certificates logic here (mocked for now)
        model.addAttribute("infoMessage", "Certificates module is currently being finalized.");
        return "student/certificates";
    }

    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        // Notifications logic here (mocked for now)
        model.addAttribute("infoMessage", "You have no new notifications.");
        return "student/notifications";
    }
}
