package com.lms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.user.dto.request.ProfileRequest;
import com.lms.user.entity.UserProfile;
import com.lms.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createProfile_ShouldReturnCreated() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        doNothing().when(userService).createProfile(anyString(), any(ProfileRequest.class));

        mockMvc.perform(post("/api/v1/users/profile")
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getMyProfile_ShouldReturnProfile() throws Exception {
        UUID authId = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        profile.setId(UUID.randomUUID());
        profile.setAuthUserId(authId);
        profile.setFirstName("John");

        when(userService.getProfile(authId.toString())).thenReturn(profile);

        mockMvc.perform(get("/api/v1/users/me")
                .header("X-User-Id", authId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    void updateMyProfile_ShouldReturnOk() throws Exception {
        ProfileRequest request = new ProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");

        doNothing().when(userService).updateProfile(anyString(), any(ProfileRequest.class));

        mockMvc.perform(put("/api/v1/users/me")
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getProfileById_ShouldReturnProfile_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        profile.setId(id);
        profile.setFirstName("John");

        when(userService.getProfileById(id.toString())).thenReturn(profile);

        mockMvc.perform(get("/api/v1/users/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    void getProfileById_ShouldReturnUnauthorized_WhenRoleIsNotAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/users/{id}", id)
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // In global exception handler this returns 401
    }

    @Test
    void getAllProfiles_ShouldReturnList_WhenRoleIsAdmin() throws Exception {
        UserProfile p1 = new UserProfile();
        p1.setId(UUID.randomUUID());
        p1.setFirstName("John");

        when(userService.getAllProfiles()).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/v1/users")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void deleteProfile_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(userService).deleteProfile(id.toString());

        mockMvc.perform(delete("/api/v1/users/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createProfile_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        ProfileRequest request = new ProfileRequest();
        // Missing firstName and lastName which are likely required by @NotBlank

        mockMvc.perform(post("/api/v1/users/profile")
                .header("X-User-Id", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
