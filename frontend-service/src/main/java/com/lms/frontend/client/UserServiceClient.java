package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.dto.UserProfileResponse;
import com.lms.frontend.dto.ProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "api-gateway", contextId = "userServiceClient", path = "/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/me")
    ApiResponse<UserProfileResponse> getCurrentUserProfile();

    @PutMapping("/me")
    ApiResponse<Void> updateMyProfile(@RequestBody ProfileRequest request);
}
