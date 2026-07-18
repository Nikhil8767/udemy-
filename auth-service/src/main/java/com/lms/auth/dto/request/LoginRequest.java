package com.lms.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(example = "student@lms.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @Schema(example = "StrongPass@123")
    @NotBlank(message = "Password is required.")
    private String password;
}
