package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "api-gateway", contextId = "adminEnrollmentServiceClient", path = "/api/v1/admin/enrollments")
public interface AdminEnrollmentServiceClient {

    @GetMapping
    ApiResponse<com.lms.frontend.dto.admin.AdminEnrollmentPaginatedResponse> listEnrollments(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @GetMapping("/reports/course/{courseId}")
    ApiResponse<com.lms.frontend.dto.admin.AdminCourseReportResponse> getCourseReport(@PathVariable("courseId") UUID courseId);

    @GetMapping("/reports/student/{studentId}")
    ApiResponse<com.lms.frontend.dto.admin.AdminStudentReportResponse> getStudentReport(@PathVariable("studentId") UUID studentId);
}
