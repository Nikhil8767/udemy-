package com.lms.auth.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InternalUserSearchResponse {
    private List<UUID> userIds;
    private List<AuthUserDto> users;
    private long totalElements;
    private int totalPages;

    @Data
    @Builder
    public static class AuthUserDto {
        private UUID id;
        private String email;
        private String role;
        private String accountStatus;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime lastLoginAt;
    }
}
