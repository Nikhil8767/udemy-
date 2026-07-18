package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.dto.EnrollmentRequest;
import com.lms.frontend.dto.EnrollmentResponse;
import com.lms.frontend.dto.LessonProgressRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "api-gateway", contextId = "enrollmentServiceClient", path = "/api/v1/enrollments")
public interface EnrollmentServiceClient {

    @PostMapping
    ApiResponse<Void> enroll(@RequestBody EnrollmentRequest request);

    @GetMapping("/my")
    ApiResponse<List<EnrollmentResponse>> getMyEnrollments();

    @GetMapping("/{courseId}")
    ApiResponse<EnrollmentResponse> getEnrollmentDetails(@PathVariable("courseId") String courseId);

    @GetMapping("/{courseId}/count")
    ApiResponse<Long> getCourseStudentCount(@PathVariable("courseId") String courseId);

    @PatchMapping("/{courseId}/complete-lesson/{lessonId}")
    ApiResponse<Void> markLessonCompleted(
            @PathVariable("courseId") String courseId,
            @PathVariable("lessonId") String lessonId,
            @RequestBody LessonProgressRequest request);

    @GetMapping("/{courseId}/status")
    ApiResponse<com.lms.frontend.dto.EnrollmentStatusDTO> getEnrollmentStatus(@PathVariable("courseId") String courseId);

    @GetMapping("/dashboard/student")
    ApiResponse<com.lms.frontend.dto.StudentDashboardResponse> getStudentDashboard();
}
