package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.ContentServiceClient;
import com.lms.frontend.client.CourseServiceClient;
import com.lms.frontend.dto.CourseRequest;
import com.lms.frontend.dto.CourseResponse;
import com.lms.frontend.dto.SectionResponse;
import com.lms.frontend.dto.ValidationReportResponse;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/tutor")
@RequiredArgsConstructor
@Slf4j
public class TutorController {

    private final CourseServiceClient courseServiceClient;
    private final ContentServiceClient contentServiceClient;
    private final com.lms.frontend.client.EnrollmentServiceClient enrollmentServiceClient;

    @GetMapping("/courses")
    public String myCourses(Model model) {
        try {
            ApiResponse<List<CourseResponse>> response = courseServiceClient.getMyCourses();
            if (response != null && response.isSuccess()) {
                List<CourseResponse> courses = response.getData();
                if (courses != null) {
                    for (CourseResponse course : courses) {
                        if ("PUBLISHED".equalsIgnoreCase(course.getCourseStatus())) {
                            try {
                                ApiResponse<Long> countRes = enrollmentServiceClient.getCourseStudentCount(course.getId().toString());
                                if (countRes != null && countRes.isSuccess()) {
                                    course.setStudentCount(countRes.getData());
                                }
                            } catch (Exception e) {
                                log.warn("Failed to load student count for course {}", course.getId());
                                course.setStudentCount(0L);
                            }
                        }
                    }
                }
                model.addAttribute("courses", courses);
            } else {
                model.addAttribute("errorMessage", "Failed to load courses.");
            }
        } catch (Exception e) {
            log.error("Error fetching tutor courses", e);
            model.addAttribute("errorMessage", "Service unavailable.");
        }
        return "tutor/courses";
    }

    @GetMapping("/courses/create")
    public String showCreateCourseForm(Model model) {
        if (!model.containsAttribute("courseRequest")) {
            CourseRequest courseRequest = new CourseRequest();
            courseRequest.setCurrency("USD");
            courseRequest.setLanguage("English");
            model.addAttribute("courseRequest", courseRequest);
        }
        model.addAttribute("categories", getCategories());
        return "tutor/course-form";
    }

    @PostMapping("/courses/create")
    public String createCourse(@Valid @ModelAttribute("courseRequest") CourseRequest courseRequest,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", getCategories());
            return "tutor/course-form";
        }
        try {
            ApiResponse<Void> response = courseServiceClient.createCourse(courseRequest);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
                return "redirect:/tutor/courses";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create course.");
            }
        } catch (FeignException e) {
            log.error("Error creating course: {}", e.contentUTF8());
            redirectAttributes.addFlashAttribute("errorMessage", extractErrorMessage(e, "Failed to create course. Please verify input."));
        } catch (Exception e) {
            log.error("Unexpected error creating course", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        redirectAttributes.addFlashAttribute("courseRequest", courseRequest);
        return "redirect:/tutor/courses/create";
    }

    @GetMapping("/courses/{id}/edit")
    public String showEditCourseForm(@PathVariable String id, Model model) {
        try {
            ApiResponse<CourseResponse> response = courseServiceClient.getCourseDetails(id);
            if (response != null && response.isSuccess() && response.getData() != null) {
                CourseResponse course = response.getData();
                CourseRequest request = new CourseRequest();
                request.setTitle(course.getTitle());
                request.setSubtitle(course.getSubtitle());
                request.setDescription(course.getDescription());
                request.setShortDescription(course.getShortDescription());
                request.setThumbnailUrl(course.getThumbnailUrl());
                request.setBannerUrl(course.getBannerUrl());
                request.setLanguage(course.getLanguage());
                request.setCourseLevel(course.getCourseLevel() != null ? course.getCourseLevel() : "BEGINNER");
                
                List<com.lms.frontend.dto.CategoryResponse> cats = getCategories();
                request.setCategoryId(course.getCategoryId() != null ? course.getCategoryId() : (cats.isEmpty() ? null : cats.get(0).getId()));
                request.setPrice(course.getPrice());
                request.setDiscountPrice(course.getDiscountPrice());
                request.setCurrency(course.getCurrency());
                request.setEstimatedDurationMinutes(course.getEstimatedDurationMinutes());
                
                model.addAttribute("courseRequest", request);
                model.addAttribute("courseId", id);
                model.addAttribute("categories", cats);
                return "tutor/course-form";
            }
        } catch (Exception e) {
            log.error("Error fetching course for edit", e);
        }
        model.addAttribute("errorMessage", "Could not load course details.");
        return "redirect:/tutor/courses";
    }

    @PostMapping("/courses/{id}/edit")
    public String editCourse(@PathVariable String id,
                             @Valid @ModelAttribute("courseRequest") CourseRequest courseRequest,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("courseId", id);
            model.addAttribute("categories", getCategories());
            return "tutor/course-form";
        }
        try {
            ApiResponse<Void> response = courseServiceClient.updateCourse(id, courseRequest);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
                return "redirect:/tutor/courses";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update course.");
            }
        } catch (FeignException e) {
            log.error("Error updating course: {}", e.contentUTF8());
            redirectAttributes.addFlashAttribute("errorMessage", extractErrorMessage(e, "Failed to update course. Please verify input."));
        } catch (Exception e) {
            log.error("Error updating course", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + id + "/edit";
    }

    @GetMapping("/courses/{id}/publish")
    public String showPublishReadinessPage(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        // Always return the page itself, never a silent redirect — the error should be visible.
        try {
            ApiResponse<CourseResponse> courseRes = courseServiceClient.getCourseDetails(id);
            if (courseRes == null || !courseRes.isSuccess() || courseRes.getData() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Course not found.");
                return "redirect:/tutor/courses";
            }
            model.addAttribute("course", courseRes.getData());
        } catch (FeignException e) {
            log.error("Error fetching course details for publish page: {}", e.contentUTF8(), e);
            redirectAttributes.addFlashAttribute("errorMessage", extractErrorMessage(e, "Failed to load course."));
            return "redirect:/tutor/courses";
        } catch (Exception e) {
            log.error("Error fetching course details for publish page", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to load course details.");
            return "redirect:/tutor/courses";
        }

        // Load validation report — on failure, provide a safe empty report with the error
        try {
            ApiResponse<ValidationReportResponse> validateRes = courseServiceClient.validateCourse(id);
            if (validateRes != null && validateRes.isSuccess() && validateRes.getData() != null) {
                model.addAttribute("report", validateRes.getData());
            } else {
                // Backend returned a non-success — build an error report
                ValidationReportResponse errorReport = buildErrorReport("Validation service returned an empty response.");
                model.addAttribute("report", errorReport);
                model.addAttribute("errorMessage", "Could not load full validation report. The course may have issues.");
            }
        } catch (FeignException e) {
            log.error("Validation Feign error: {}", e.contentUTF8(), e);
            String errMsg = extractErrorMessage(e, "Validation service error.");
            ValidationReportResponse errorReport = buildErrorReport(errMsg);
            model.addAttribute("report", errorReport);
            model.addAttribute("errorMessage", errMsg);
        } catch (Exception e) {
            log.error("Error fetching validation report", e);
            ValidationReportResponse errorReport = buildErrorReport("Validation service unavailable: " + e.getMessage());
            model.addAttribute("report", errorReport);
            model.addAttribute("errorMessage", "Validation service is unavailable. Please ensure all services are running.");
        }
        return "tutor/publish-course";
    }

    @PostMapping("/courses/{id}/publish")
    public String publishCourse(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = courseServiceClient.publishCourse(id);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Course published successfully!");
                return "redirect:/tutor/courses";
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to publish course.");
            }
        } catch (FeignException e) {
            log.error("Error publishing course: {}", e.contentUTF8());
            String errorMsg = extractErrorMessage(e, "Validation failed.");
            java.util.List<String> validationErrors = java.util.Arrays.asList(errorMsg.split("\\|"));
            redirectAttributes.addFlashAttribute("validationErrors", validationErrors);
            redirectAttributes.addFlashAttribute("errorMessage", "Course failed to publish due to validation errors.");
        } catch (Exception e) {
            log.error("Error publishing course", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + id + "/publish";
    }

    @PostMapping("/courses/{id}/delete")
    public String deleteCourse(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = courseServiceClient.deleteCourse(id);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete course.");
            }
        } catch (Exception e) {
            log.error("Error deleting course", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses";
    }

    @GetMapping("/courses/{id}/curriculum")
    public String manageCurriculum(@PathVariable String id, Model model) {
        try {
            ApiResponse<CourseResponse> courseRes = courseServiceClient.getCourseDetails(id);
            if (courseRes != null && courseRes.isSuccess() && courseRes.getData() != null) {
                model.addAttribute("course", courseRes.getData());
                
                ApiResponse<List<SectionResponse>> sectionsRes = contentServiceClient.getCourseSections(id);
                if (sectionsRes != null && sectionsRes.isSuccess()) {
                    List<SectionResponse> sections = sectionsRes.getData();
                    // Load lessons for each section
                    if (sections != null) {
                        for (SectionResponse section : sections) {
                            try {
                                var lessonsRes = contentServiceClient.getSectionLessons(section.getId().toString());
                                if (lessonsRes != null && lessonsRes.isSuccess() && lessonsRes.getData() != null) {
                                    List<com.lms.frontend.dto.LessonResponse> lessons = lessonsRes.getData();
                                    for (com.lms.frontend.dto.LessonResponse lesson : lessons) {
                                        try {
                                            var resRes = contentServiceClient.getLessonResources(lesson.getId().toString());
                                            if (resRes != null && resRes.isSuccess()) {
                                                lesson.setResources(resRes.getData());
                                            }
                                        } catch (Exception ex) {
                                            log.warn("Failed to load resources for lesson {}", lesson.getId());
                                        }
                                    }
                                    section.setLessons(lessons);
                                }
                            } catch (Exception ex) {
                                log.warn("Failed to load lessons for section {}", section.getId());
                            }
                        }
                    }
                    model.addAttribute("sections", sections);
                }
            } else {
                model.addAttribute("errorMessage", "Course not found.");
                return "redirect:/tutor/courses";
            }
        } catch (Exception e) {
            log.error("Error fetching curriculum", e);
            model.addAttribute("errorMessage", "Failed to load curriculum.");
            return "redirect:/tutor/courses";
        }
        return "tutor/curriculum";
    }

    @GetMapping("/courses/{id}/preview")
    public String previewCourse(@PathVariable String id, @org.springframework.web.bind.annotation.RequestParam(required = false) String lessonId, Model model) {
        try {
            ApiResponse<CourseResponse> courseRes = courseServiceClient.getCourseDetails(id);
            if (courseRes == null || !courseRes.isSuccess() || courseRes.getData() == null) {
                model.addAttribute("errorMessage", "Course not found.");
                return "redirect:/tutor/courses";
            }
            model.addAttribute("course", courseRes.getData());

            ApiResponse<List<SectionResponse>> sectionsRes = contentServiceClient.getCourseSections(id);
            if (sectionsRes != null && sectionsRes.isSuccess() && sectionsRes.getData() != null) {
                List<SectionResponse> sections = sectionsRes.getData();
                
                com.lms.frontend.dto.LessonResponse currentLesson = null;
                for (SectionResponse section : sections) {
                    ApiResponse<List<com.lms.frontend.dto.LessonResponse>> lessonsRes = contentServiceClient.getSectionLessons(section.getId().toString());
                    if (lessonsRes != null && lessonsRes.isSuccess() && lessonsRes.getData() != null) {
                        List<com.lms.frontend.dto.LessonResponse> lessons = lessonsRes.getData();
                        for (com.lms.frontend.dto.LessonResponse lesson : lessons) {
                            try {
                                ApiResponse<List<com.lms.frontend.dto.ResourceResponse>> resRes = contentServiceClient.getLessonResources(lesson.getId().toString());
                                if (resRes != null && resRes.isSuccess()) {
                                    lesson.setResources(resRes.getData());
                                }
                            } catch (Exception ex) {
                                log.warn("Failed to load resources for lesson {}", lesson.getId());
                            }
                        }
                        section.setLessons(lessons);
                        
                        for (com.lms.frontend.dto.LessonResponse lesson : lessons) {
                            if (lessonId != null && lesson.getId().toString().equals(lessonId)) {
                                currentLesson = lesson;
                            } else if (currentLesson == null && lessonId == null) {
                                currentLesson = lesson;
                            }
                        }
                    }
                }
                
                model.addAttribute("sections", sections);
                model.addAttribute("currentLesson", currentLesson);
            }
        } catch (Exception e) {
            log.error("Error loading preview page", e);
            model.addAttribute("errorMessage", "Failed to load preview environment.");
            return "redirect:/tutor/courses";
        }
        return "course/learning-page"; // Reuse the student view exactly!
    }

    // --- Curriculum Management Actions ---

    @PostMapping("/courses/{courseId}/archive")
    public String archiveCourse(@PathVariable String courseId, RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = courseServiceClient.archiveCourse(courseId);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Course archived successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to archive course.");
            }
        } catch (Exception e) {
            log.error("Error archiving course", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses";
    }

    @PostMapping("/courses/{courseId}/sections")
    public String createSection(@PathVariable String courseId, 
                                @ModelAttribute com.lms.frontend.dto.SectionRequest request,
                                RedirectAttributes redirectAttributes) {
        request.setCourseId(courseId);
        try {
            ApiResponse<Void> response = contentServiceClient.createSection(request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Section created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create section.");
            }
        } catch (Exception e) {
            log.error("Error creating section", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/sections/{sectionId}/edit")
    public String editSection(@PathVariable String courseId, 
                              @PathVariable String sectionId,
                              @ModelAttribute com.lms.frontend.dto.SectionRequest request,
                              RedirectAttributes redirectAttributes) {
        request.setCourseId(courseId);
        try {
            ApiResponse<Void> response = contentServiceClient.updateSection(sectionId, request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Section updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update section.");
            }
        } catch (Exception e) {
            log.error("Error updating section", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/sections/{sectionId}/delete")
    public String deleteSection(@PathVariable String courseId, 
                                @PathVariable String sectionId,
                                RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = contentServiceClient.deleteSection(sectionId);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Section deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete section.");
            }
        } catch (Exception e) {
            log.error("Error deleting section", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/lessons")
    public String createLesson(@PathVariable String courseId, 
                               @ModelAttribute com.lms.frontend.dto.LessonRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = contentServiceClient.createLesson(request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Lesson created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to create lesson.");
            }
        } catch (Exception e) {
            log.error("Error creating lesson", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/edit")
    public String editLesson(@PathVariable String courseId, 
                               @PathVariable String lessonId,
                               @ModelAttribute com.lms.frontend.dto.LessonRequest request,
                               RedirectAttributes redirectAttributes) {
        request.setSectionId(null); // Assuming sectionId shouldn't change or is set appropriately
        try {
            ApiResponse<Void> response = contentServiceClient.updateLesson(lessonId, request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Lesson updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update lesson.");
            }
        } catch (Exception e) {
            log.error("Error updating lesson", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/lessons/{lessonId}/delete")
    public String deleteLesson(@PathVariable String courseId, 
                               @PathVariable String lessonId,
                               RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = contentServiceClient.deleteLesson(lessonId);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Lesson deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete lesson.");
            }
        } catch (Exception e) {
            log.error("Error deleting lesson", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/resources")
    public String createResource(@PathVariable String courseId, 
                                 @ModelAttribute com.lms.frontend.dto.ResourceRequest request,
                                 RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = contentServiceClient.createResource(request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Resource added successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to add resource.");
            }
        } catch (Exception e) {
            log.error("Error creating resource", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/resources/{resourceId}/edit")
    public String editResource(@PathVariable String courseId, 
                               @PathVariable String resourceId,
                               @ModelAttribute com.lms.frontend.dto.ResourceRequest request,
                               RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = contentServiceClient.updateResource(resourceId, request);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Resource updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to update resource.");
            }
        } catch (Exception e) {
            log.error("Error updating resource", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    @PostMapping("/courses/{courseId}/resources/{resourceId}/delete")
    public String deleteResource(@PathVariable String courseId, 
                                 @PathVariable String resourceId,
                                 RedirectAttributes redirectAttributes) {
        try {
            ApiResponse<Void> response = contentServiceClient.deleteResource(resourceId);
            if (response != null && response.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", "Resource deleted successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete resource.");
            }
        } catch (Exception e) {
            log.error("Error deleting resource", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Service unavailable.");
        }
        return "redirect:/tutor/courses/" + courseId + "/curriculum";
    }

    // --- Helpers ---
    
    private List<com.lms.frontend.dto.CategoryResponse> getCategories() {
        try {
            ApiResponse<List<com.lms.frontend.dto.CategoryResponse>> response = courseServiceClient.getCategories();
            if (response != null && response.isSuccess()) {
                return response.getData();
            }
        } catch (Exception e) {
            log.error("Error fetching categories", e);
        }
        return java.util.Collections.emptyList();
    }

    private ValidationReportResponse buildErrorReport(String errorMessage) {
        ValidationReportResponse report = new ValidationReportResponse();
        report.setErrors(java.util.Collections.singletonList(errorMessage));
        report.setReadyToPublish(false);
        return report;
    }

    private String extractErrorMessage(FeignException e, String defaultMessage) {
        try {
            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(e.contentUTF8());
            if (root.has("message")) {
                return root.get("message").asText();
            }
        } catch (Exception ex) {
            log.warn("Could not parse Feign exception body", ex);
        }
        return defaultMessage;
    }
}
