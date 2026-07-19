package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "api-gateway", contextId = "adminCourseServiceClient", path = "/api/v1/admin/courses")
public interface AdminCourseServiceClient {

    @GetMapping("/statistics")
    ApiResponse<com.lms.frontend.dto.admin.CourseStatisticsResponse> getStatistics();

    @GetMapping
    ApiResponse<com.lms.frontend.dto.admin.AdminCoursePaginatedResponse> listCourses(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "categoryId", required = false) UUID categoryId,
            @RequestParam(value = "instructorId", required = false) UUID instructorId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @PatchMapping("/{id}/publish")
    ApiResponse<Void> publishCourse(@PathVariable("id") UUID id);

    @PatchMapping("/{id}/archive")
    ApiResponse<Void> archiveCourse(@PathVariable("id") UUID id);

    @PatchMapping("/{id}/feature")
    ApiResponse<Void> featureCourse(@PathVariable("id") UUID id);

    @DeleteMapping("/{id}")
    ApiResponse<Void> deleteCourse(@PathVariable("id") UUID id);
}
