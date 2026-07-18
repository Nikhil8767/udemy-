package com.lms.enrollment.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.enrollment.dto.response.CourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "course-service", path = "/api/v1/courses")
public interface CourseServiceClient {
    @GetMapping("/{id}")
    ApiResponse<CourseResponse> getCourseDetails(
            @PathVariable("id") String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role
    );
}
