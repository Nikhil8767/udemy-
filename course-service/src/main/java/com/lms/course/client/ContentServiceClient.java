package com.lms.course.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.course.dto.response.SectionResponse;
import com.lms.course.dto.response.LessonResponse;
import com.lms.course.dto.response.ResourceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * Feign client for course-service → content-service service-to-service calls.
 *
 * NOTE: These calls go directly between services (not through the API Gateway).
 * The X-User-Id and X-User-Role headers are passed explicitly from the calling method
 * so that content-service access-control checks still work correctly.
 */
@FeignClient(name = "content-service", path = "/api/v1/content")
public interface ContentServiceClient {

    @GetMapping("/sections/course/{courseId}")
    ApiResponse<List<SectionResponse>> getCourseSections(@PathVariable("courseId") String courseId);

    @GetMapping("/lessons/section/{sectionId}")
    ApiResponse<List<LessonResponse>> getSectionLessons(
            @PathVariable("sectionId") String sectionId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role);

    @GetMapping("/resources/{lessonId}")
    ApiResponse<List<ResourceResponse>> getLessonResources(
            @PathVariable("lessonId") String lessonId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role);
}
