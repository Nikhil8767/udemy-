package com.lms.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {
    private String accessToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private long expiresIn;
    private String userId;
    private String email;
    private String role;
    private String accountStatus;
}
