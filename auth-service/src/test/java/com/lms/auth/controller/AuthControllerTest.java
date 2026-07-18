package com.lms.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RegisterRequest;
import com.lms.auth.dto.response.JwtResponse;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.jwt.JwtUtils;
import com.lms.auth.service.AuthService;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disables security filters for unit testing controllers directly
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils; // Required by context if any filters are accidentally loaded

    @Test
    void register_ShouldReturnCreated_WhenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setPassword("Password123!");
        request.setRole(Role.ROLE_STUDENT);

        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("Password123!");

        JwtResponse response = JwtResponse.builder()
                .accessToken("jwt-token")
                .email("test@test.com")
                .role("ROLE_STUDENT")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"));
    }

    @Test
    void me_ShouldReturnProfile_WhenAuthenticated() throws Exception {
        UserCredential user = new UserCredential();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setRole(Role.ROLE_STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);

        when(authService.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(get("/api/v1/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setPassword("weak");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
