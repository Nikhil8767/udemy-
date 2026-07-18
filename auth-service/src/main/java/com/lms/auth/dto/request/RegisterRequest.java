package com.lms.auth.dto.request;

import com.lms.common.enums.Role;
import com.lms.common.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest {
    @Schema(example = "student@lms.com")
    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @Schema(example = "StrongPass@123")
    @NotBlank(message = "Password is required.")
    @ValidPassword(message = "Password must contain at least 8 characters, one uppercase, one lowercase, one digit, and one special character.")
    private String password;

    @Schema(example = "ROLE_STUDENT")
    @NotNull(message = "Role is required.")
    private Role role;
}
