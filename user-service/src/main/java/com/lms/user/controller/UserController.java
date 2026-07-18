package com.lms.user.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.user.dto.request.ProfileRequest;
import com.lms.user.dto.response.ProfileResponse;
import com.lms.user.entity.UserProfile;
import com.lms.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.lms.user.service.ProfileCompletionService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for managing user profiles")
public class UserController {

    private final UserService userService;
    private final ProfileCompletionService profileCompletionService;

    @PostMapping("/profile")
    @Operation(summary = "Create profile for authenticated user")
    public ResponseEntity<ApiResponse<Void>> createProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ProfileRequest request) {
        userService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Profile created successfully.")
                        .build()
        );
    }

    @GetMapping("/me")
    @Operation(summary = "Return authenticated user's profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-User-Role", defaultValue = "ROLE_STUDENT") String role) {
        UserProfile profile = userService.getProfile(userId);
        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .success(true)
                        .message("Profile retrieved successfully.")
                        .data(mapToResponse(profile, role))
                        .build()
        );
    }

    @PutMapping("/me")
    @Operation(summary = "Update authenticated user's profile")
    public ResponseEntity<ApiResponse<Void>> updateMyProfile(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody ProfileRequest request) {
        userService.updateProfile(userId, request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Profile updated successfully.")
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID (Admin only)")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileById(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String id) {
        checkAdmin(role);
        UserProfile profile = userService.getProfileById(id);
        return ResponseEntity.ok(
                ApiResponse.<ProfileResponse>builder()
                        .success(true)
                        .message("Profile retrieved successfully.")
                        .data(mapToResponse(profile, "ROLE_STUDENT"))
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get all user profiles (Admin only)")
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> getAllProfiles(
            @RequestHeader("X-User-Role") String role) {
        checkAdmin(role);
        List<ProfileResponse> profiles = userService.getAllProfiles().stream()
                .map(p -> mapToResponse(p, "ROLE_STUDENT"))
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<ProfileResponse>>builder()
                        .success(true)
                        .message("Profiles fetched successfully.")
                        .data(profiles)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user profile by ID (Admin only)")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String id) {
        checkAdmin(role);
        userService.deleteProfile(id);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Profile deleted successfully.")
                        .build()
        );
    }

    private void checkAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    private ProfileResponse mapToResponse(UserProfile profile, String role) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .authUserId(profile.getAuthUserId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .phoneNumber(profile.getPhoneNumber())
                .dateOfBirth(profile.getDateOfBirth())
                .gender(profile.getGender())
                .bio(profile.getBio())
                .profileImageUrl(profile.getProfileImageUrl())
                .country(profile.getCountry())
                .state(profile.getState())
                .city(profile.getCity())
                .zipCode(profile.getZipCode())
                .address(profile.getAddress())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .websiteUrl(profile.getWebsiteUrl())
                .displayName(profile.getDisplayName())
                .about(profile.getAbout())
                .qualifications(profile.getQualifications())
                .teachingExperience(profile.getTeachingExperience())
                .skills(profile.getSkills())
                .preferredLanguage(profile.getPreferredLanguage())
                .completionPercentage(profileCompletionService.calculateCompletionPercentage(profile, role))
                .build();
    }
}
