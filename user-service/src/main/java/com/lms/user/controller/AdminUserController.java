package com.lms.user.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.common.exception.UnauthorizedException;
import com.lms.user.client.AuthServiceClient;
import com.lms.user.dto.response.InternalUserSearchResponse;
import com.lms.user.entity.UserProfile;
import com.lms.user.repository.UserProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin Users", description = "Admin endpoints for user management")
public class AdminUserController {

    private final UserProfileRepository userProfileRepository;
    private final AuthServiceClient authServiceClient;

    private void verifyAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    @Data
    public static class AdminUserSummaryResponse {
        private UUID authUserId;
        private String firstName;
        private String lastName;
        private String profileImageUrl;
        private String email;
        private String role;
        private String accountStatus;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime lastLoginAt;
    }

    @Data
    public static class AdminUserPaginatedResponse {
        private List<AdminUserSummaryResponse> content;
        private long totalElements;
        private int totalPages;
    }

    @Data
    public static class AdminUserDetailsResponse {
        private UUID authUserId;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String profileImageUrl;
        private String country;
        private String state;
        private String city;
    }

    @GetMapping
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<AdminUserPaginatedResponse>> listUsers(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        verifyAdmin(roleHeader);

        InternalUserSearchResponse searchResponse = authServiceClient.searchUsers(role, status, page, size);
        
        java.util.List<UUID> userIds = searchResponse.getUsers().stream().map(InternalUserSearchResponse.AuthUserDto::getId).toList();
        
        java.util.Map<UUID, UserProfile> profileMap = userProfileRepository.findAllByAuthUserIdIn(userIds)
                .stream().collect(java.util.stream.Collectors.toMap(UserProfile::getAuthUserId, p -> p));

        List<AdminUserSummaryResponse> content = searchResponse.getUsers().stream().map(authUser -> {
                    AdminUserSummaryResponse dto = new AdminUserSummaryResponse();
                    dto.setAuthUserId(authUser.getId());
                    dto.setEmail(authUser.getEmail());
                    dto.setRole(authUser.getRole());
                    dto.setAccountStatus(authUser.getAccountStatus());
                    dto.setCreatedAt(authUser.getCreatedAt());
                    dto.setLastLoginAt(authUser.getLastLoginAt());

                    UserProfile profile = profileMap.get(authUser.getId());
                    if (profile != null) {
                        dto.setFirstName(profile.getFirstName());
                        dto.setLastName(profile.getLastName());
                        dto.setProfileImageUrl(profile.getProfileImageUrl());
                    }
                    return dto;
                }).toList();

        AdminUserPaginatedResponse responseData = new AdminUserPaginatedResponse();
        responseData.setContent(content);
        responseData.setTotalElements(searchResponse.getTotalElements());
        responseData.setTotalPages(searchResponse.getTotalPages());

        return ResponseEntity.ok(ApiResponse.<AdminUserPaginatedResponse>builder()
                .success(true)
                .message("Users fetched successfully.")
                .data(responseData)
                .build());
    }

    @GetMapping("/{id}")
    @Operation(summary = "User Details")
    public ResponseEntity<ApiResponse<AdminUserDetailsResponse>> getUserDetails(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        UserProfile profile = userProfileRepository.findByAuthUserId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        
        AdminUserDetailsResponse response = new AdminUserDetailsResponse();
        response.setAuthUserId(profile.getAuthUserId());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setProfileImageUrl(profile.getProfileImageUrl());
        response.setCountry(profile.getCountry());
        response.setState(profile.getState());
        response.setCity(profile.getCity());

        return ResponseEntity.ok(ApiResponse.<AdminUserDetailsResponse>builder()
                .success(true)
                .message("User details fetched successfully.")
                .data(response)
                .build());
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate User")
    public ResponseEntity<ApiResponse<Void>> activateUser(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        authServiceClient.updateStatus(id, AccountStatus.ACTIVE);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User activated successfully.")
                .build());
    }

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate User")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        authServiceClient.updateStatus(id, AccountStatus.SUSPENDED);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User deactivated successfully.")
                .build());
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Soft Delete User")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        authServiceClient.updateStatus(id, AccountStatus.SUSPENDED);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully.")
                .build());
    }
}
