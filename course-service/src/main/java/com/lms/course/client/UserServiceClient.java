package com.lms.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.lms.common.dto.response.ApiResponse;
import com.lms.course.dto.response.ProfileResponse;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", path = "/api/v1/users")
public interface UserServiceClient {
    
    @GetMapping("/me")
    ApiResponse<ProfileResponse> getMyProfile(@RequestHeader("X-User-Id") String userId);
}
