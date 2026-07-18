package com.lms.frontend.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.frontend.dto.JwtResponse;
import com.lms.frontend.dto.LoginRequest;
import com.lms.frontend.dto.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "api-gateway", path = "/api/v1/auth")
public interface AuthServiceClient {

    @PostMapping("/login")
    ApiResponse<JwtResponse> login(@RequestBody LoginRequest request);

    @PostMapping("/register")
    ApiResponse<Void> register(@RequestBody RegisterRequest request);
}
