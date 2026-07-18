package com.lms.auth.controller;

import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RegisterRequest;
import com.lms.auth.dto.response.JwtResponse;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.service.AuthService;
import com.lms.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user (Student or Tutor)")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Registration completed successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT token")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("=== AuthController: /login endpoint hit for email: {} ===", request.getEmail());
        JwtResponse jwtResponse = authService.login(request);
        log.info("=== AuthController: Login SUCCEEDED. Building ApiResponse. ===");
        ApiResponse<JwtResponse> response = ApiResponse.<JwtResponse>builder()
                .success(true)
                .message("Login successful.")
                .data(jwtResponse)
                .build();
        log.info("=== AuthController: Returning 200 OK with JWT. ===");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user details", security = @SecurityRequirement(name = "Bearer Authentication"))
    public ResponseEntity<ApiResponse<Map<String, Object>>> me() {
        UserCredential user = authService.getAuthenticatedUser();
        Map<String, Object> userData = Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole(),
                "accountStatus", user.getAccountStatus()
        );
        ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Profile fetched successfully.")
                .data(userData)
                .build();
        return ResponseEntity.ok(response);
    }
}
