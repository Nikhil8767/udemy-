package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.dto.LessonResponse;
import com.lms.frontend.dto.SectionResponse;
import com.lms.frontend.dto.LessonRequest;
import com.lms.frontend.dto.ResourceRequest;
import com.lms.frontend.dto.ResourceResponse;
import com.lms.frontend.dto.SectionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "api-gateway", contextId = "contentServiceClient", path = "/api/v1/content")
public interface ContentServiceClient {

    // --- Sections ---
    @PostMapping("/sections")
    ApiResponse<Void> createSection(@RequestBody SectionRequest request);

    @PutMapping("/sections/{id}")
    ApiResponse<Void> updateSection(@PathVariable("id") String id, @RequestBody SectionRequest request);

    @DeleteMapping("/sections/{id}")
    ApiResponse<Void> deleteSection(@PathVariable("id") String id);

    @GetMapping("/sections/course/{courseId}")
    ApiResponse<List<SectionResponse>> getCourseSections(@PathVariable("courseId") String courseId);

    // --- Lessons ---
    @PostMapping("/lessons")
    ApiResponse<Void> createLesson(@RequestBody LessonRequest request);

    @PutMapping("/lessons/{id}")
    ApiResponse<Void> updateLesson(@PathVariable("id") String id, @RequestBody LessonRequest request);

    @DeleteMapping("/lessons/{id}")
    ApiResponse<Void> deleteLesson(@PathVariable("id") String id);

    @GetMapping("/lessons/section/{sectionId}")
    ApiResponse<List<LessonResponse>> getSectionLessons(@PathVariable("sectionId") String sectionId);

    @GetMapping("/lessons/{id}")
    ApiResponse<LessonResponse> getLesson(@PathVariable("id") String id);

    // --- Resources ---
    @PostMapping("/resources")
    ApiResponse<Void> createResource(@RequestBody ResourceRequest request);

    @GetMapping("/resources/{lessonId}")
    ApiResponse<List<ResourceResponse>> getLessonResources(@PathVariable("lessonId") String lessonId);

    @PutMapping("/resources/{id}")
    ApiResponse<Void> updateResource(@PathVariable("id") String id, @RequestBody ResourceRequest request);

    @DeleteMapping("/resources/{id}")
    ApiResponse<Void> deleteResource(@PathVariable("id") String id);
}
