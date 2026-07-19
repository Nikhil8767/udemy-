package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminUserSummaryResponse {
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
