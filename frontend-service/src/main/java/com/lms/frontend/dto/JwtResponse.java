package com.lms.frontend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class JwtResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String userId;
    private String email;
    private String role;
    private String accountStatus;
}
