package com.lms.user.dto.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class InternalUserSearchResponse {
    private List<UUID> userIds;
    private List<AuthUserDto> users;
    private long totalElements;
    private int totalPages;

    @Data
    public static class AuthUserDto {
        private UUID id;
        private String email;
        private String role;
        private String accountStatus;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime lastLoginAt;
    }
}
