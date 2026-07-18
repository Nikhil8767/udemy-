package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.dto.CourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import com.lms.frontend.dto.CategoryResponse;

@FeignClient(name = "api-gateway", contextId = "courseServiceClient", path = "/api/v1/courses")
public interface CourseServiceClient {

    @GetMapping("/categories")
    ApiResponse<List<CategoryResponse>> getCategories();

    @GetMapping("/published")
    ApiResponse<List<CourseResponse>> getPublishedCourses();

    @GetMapping("/{id}")
    ApiResponse<CourseResponse> getCourseDetails(@PathVariable("id") String id);

    @GetMapping("/my")
    ApiResponse<List<CourseResponse>> getMyCourses();

    @PostMapping
    ApiResponse<Void> createCourse(@org.springframework.web.bind.annotation.RequestBody com.lms.frontend.dto.CourseRequest request);

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    ApiResponse<Void> updateCourse(@PathVariable("id") String id, @org.springframework.web.bind.annotation.RequestBody com.lms.frontend.dto.CourseRequest request);

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/publish")
    ApiResponse<Void> publishCourse(@PathVariable("id") String id);

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/archive")
    ApiResponse<Void> archiveCourse(@PathVariable("id") String id);

    @org.springframework.web.bind.annotation.DeleteMapping("/{id}")
    ApiResponse<Void> deleteCourse(@PathVariable("id") String id);

    @GetMapping("/{id}/validate")
    ApiResponse<com.lms.frontend.dto.ValidationReportResponse> validateCourse(@PathVariable("id") String id);

}
