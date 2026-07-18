package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.ContentServiceClient;
import com.lms.frontend.client.CourseServiceClient;
import com.lms.frontend.dto.CourseResponse;
import com.lms.frontend.dto.LessonResponse;
import com.lms.frontend.dto.SectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseServiceClient courseServiceClient;
    private final ContentServiceClient contentServiceClient;

    @GetMapping
    public String listPublishedCourses(Model model) {
        try {
            ApiResponse<List<CourseResponse>> response = courseServiceClient.getPublishedCourses();
            if (response != null && response.isSuccess()) {
                model.addAttribute("courses", response.getData());
            } else {
                model.addAttribute("errorMessage", "Failed to load courses.");
            }
        } catch (Exception e) {
            log.error("Error fetching published courses", e);
            model.addAttribute("errorMessage", "Service unavailable.");
        }
        return "student/courses";
    }

    @GetMapping("/{id}")
    public String getCourseDetails(@PathVariable String id, Model model) {
        try {
            ApiResponse<CourseResponse> response = courseServiceClient.getCourseDetails(id);
            if (response != null && response.isSuccess() && response.getData() != null) {
                model.addAttribute("course", response.getData());
            } else {
                model.addAttribute("errorMessage", "Course not found.");
            }
        } catch (Exception e) {
            log.error("Error fetching course details", e);
            model.addAttribute("errorMessage", "Failed to load course details. Please try again later.");
        }
        return "course/details";
    }

    @GetMapping("/{id}/learn")
    public String getLearningPage(@PathVariable String id, @RequestParam(required = false) String lessonId, Model model) {
        try {
            ApiResponse<CourseResponse> courseRes = courseServiceClient.getCourseDetails(id);
            if (courseRes == null || !courseRes.isSuccess() || courseRes.getData() == null) {
                model.addAttribute("errorMessage", "Course not found.");
                return "course/details";
            }
            model.addAttribute("course", courseRes.getData());

            ApiResponse<List<SectionResponse>> sectionsRes = contentServiceClient.getCourseSections(id);
            if (sectionsRes != null && sectionsRes.isSuccess() && sectionsRes.getData() != null) {
                List<SectionResponse> sections = sectionsRes.getData();
                
                // Fetch lessons and resources for each section
                LessonResponse currentLesson = null;
                for (SectionResponse section : sections) {
                    ApiResponse<List<LessonResponse>> lessonsRes = contentServiceClient.getSectionLessons(section.getId().toString());
                    if (lessonsRes != null && lessonsRes.isSuccess() && lessonsRes.getData() != null) {
                        List<LessonResponse> lessons = lessonsRes.getData();
                        
                        // Load resources for each lesson
                        for (LessonResponse lesson : lessons) {
                            try {
                                ApiResponse<List<com.lms.frontend.dto.ResourceResponse>> resRes = contentServiceClient.getLessonResources(lesson.getId().toString());
                                if (resRes != null && resRes.isSuccess() && resRes.getData() != null) {
                                    lesson.setResources(resRes.getData());
                                }
                            } catch (Exception ex) {
                                log.warn("Failed to load resources for lesson {}", lesson.getId());
                            }
                        }
                        
                        section.setLessons(lessons);
                        
                        // Select current lesson to play
                        for (LessonResponse lesson : lessons) {
                            if (lessonId != null && lesson.getId().toString().equals(lessonId)) {
                                currentLesson = lesson;
                            } else if (currentLesson == null && lessonId == null) {
                                // Default to first lesson
                                currentLesson = lesson;
                            }
                        }
                    }
                }
                
                model.addAttribute("sections", sections);
                model.addAttribute("currentLesson", currentLesson);
            }
        } catch (Exception e) {
            log.error("Error loading learning page", e);
            model.addAttribute("errorMessage", "Failed to load learning environment.");
        }
        return "course/learning-page";
    }
}
