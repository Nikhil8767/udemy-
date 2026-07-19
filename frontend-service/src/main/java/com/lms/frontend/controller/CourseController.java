package com.lms.frontend.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.client.ContentServiceClient;
import com.lms.frontend.client.CourseServiceClient;
import com.lms.frontend.client.EnrollmentServiceClient;
import com.lms.frontend.dto.CourseResponse;
import com.lms.frontend.dto.LessonResponse;
import com.lms.frontend.dto.SectionResponse;
import com.lms.frontend.dto.EnrollmentStatusDTO;
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
    private final EnrollmentServiceClient enrollmentServiceClient;

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
                return "course/details";
            }

            try {
                ApiResponse<EnrollmentStatusDTO> statusRes = enrollmentServiceClient.getEnrollmentStatus(id);
                if (statusRes != null && statusRes.isSuccess() && statusRes.getData() != null) {
                    model.addAttribute("enrollmentStatus", statusRes.getData());
                }
            } catch (Exception e) {
                log.warn("Failed to get enrollment status for course {}", id);
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
            
            EnrollmentStatusDTO status = null;
            try {
                ApiResponse<EnrollmentStatusDTO> statusRes = enrollmentServiceClient.getEnrollmentStatus(id);
                if (statusRes != null && statusRes.isSuccess() && statusRes.getData() != null) {
                    status = statusRes.getData();
                    model.addAttribute("enrollmentStatus", status);
                }
            } catch (Exception e) {
                log.warn("Failed to get enrollment status");
            }
            
            String targetLessonId = lessonId;
            if (targetLessonId == null && status != null && status.getLastAccessedLessonId() != null) {
                targetLessonId = status.getLastAccessedLessonId().toString();
            }

            ApiResponse<List<SectionResponse>> sectionsRes = contentServiceClient.getCourseSections(id);
            if (sectionsRes != null && sectionsRes.isSuccess() && sectionsRes.getData() != null) {
                List<SectionResponse> sections = sectionsRes.getData();
                
                LessonResponse currentLesson = null;
                List<LessonResponse> allLessons = new java.util.ArrayList<>();
                for (SectionResponse section : sections) {
                    ApiResponse<List<LessonResponse>> lessonsRes = contentServiceClient.getSectionLessons(section.getId().toString());
                    if (lessonsRes != null && lessonsRes.isSuccess() && lessonsRes.getData() != null) {
                        List<LessonResponse> lessons = lessonsRes.getData();
                        
                        for (LessonResponse lesson : lessons) {
                            try {
                                ApiResponse<List<com.lms.frontend.dto.ResourceResponse>> resRes = contentServiceClient.getLessonResources(lesson.getId().toString());
                                if (resRes != null && resRes.isSuccess() && resRes.getData() != null) {
                                    lesson.setResources(resRes.getData());
                                }
                            } catch (Exception ex) {
                                log.warn("Failed to load resources for lesson {}", lesson.getId());
                            }
                            allLessons.add(lesson);
                        }
                        section.setLessons(lessons);
                        section.setLessonCount(lessons.size());
                        int duration = 0;
                        for (LessonResponse l : lessons) {
                            if (l.getDurationMinutes() != null) duration += l.getDurationMinutes();
                        }
                        section.setTotalDurationMinutes(duration);
                    }
                }
                
                for (LessonResponse lesson : allLessons) {
                    if (targetLessonId != null && lesson.getId().toString().equals(targetLessonId)) {
                        currentLesson = lesson;
                    } else if (currentLesson == null && targetLessonId == null) {
                        currentLesson = lesson;
                    }
                }

                String previousLessonId = null;
                String nextLessonId = null;
                if (currentLesson != null) {
                    int currentIndex = -1;
                    for (int i = 0; i < allLessons.size(); i++) {
                        if (allLessons.get(i).getId().equals(currentLesson.getId())) {
                            currentIndex = i;
                            break;
                        }
                    }
                    if (currentIndex > 0) {
                        previousLessonId = allLessons.get(currentIndex - 1).getId().toString();
                    }
                    if (currentIndex != -1 && currentIndex < allLessons.size() - 1) {
                        nextLessonId = allLessons.get(currentIndex + 1).getId().toString();
                    }
                }
                java.util.Map<java.util.UUID, Integer> lessonIndexes = new java.util.HashMap<>();
                for (int i = 0; i < allLessons.size(); i++) {
                    lessonIndexes.put(allLessons.get(i).getId(), i);
                }
                
                model.addAttribute("sections", sections);
                model.addAttribute("currentLesson", currentLesson);
                model.addAttribute("allLessons", allLessons);
                model.addAttribute("lessonIndexes", lessonIndexes);
                model.addAttribute("allLessons", allLessons);
                model.addAttribute("previousLessonId", previousLessonId);
                model.addAttribute("nextLessonId", nextLessonId);

                // Calculate first incomplete lesson index for sequential locking
                int firstIncompleteLessonIndex = allLessons.size();
                if (status != null && status.getCompletedLessonIds() != null) {
                    for (int i = 0; i < allLessons.size(); i++) {
                        if (!status.getCompletedLessonIds().contains(allLessons.get(i).getId())) {
                            firstIncompleteLessonIndex = i;
                            break;
                        }
                    }
                } else {
                    firstIncompleteLessonIndex = allLessons.size();
                }
                model.addAttribute("firstIncompleteLessonIndex", firstIncompleteLessonIndex);
            }
        } catch (Exception e) {
            log.error("Error loading learning page", e);
            model.addAttribute("errorMessage", "Failed to load learning environment.");
        }
        return "course/learning-page";
    }
}
