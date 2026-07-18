package com.lms.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import com.lms.user.client.AuthServiceClient;
import com.lms.user.dto.response.InternalUserSearchResponse;
import com.lms.user.entity.UserProfile;
import com.lms.user.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserProfileRepository userProfileRepository;

    @MockBean
    private AuthServiceClient authServiceClient;

    @Test
    void listUsers_ShouldReturnPaginatedList_WhenRoleIsAdmin() throws Exception {
        UUID authId = UUID.randomUUID();
        InternalUserSearchResponse searchResponse = new InternalUserSearchResponse();
        searchResponse.setUserIds(List.of(authId));
        searchResponse.setTotalElements(1L);
        searchResponse.setTotalPages(1);

        when(authServiceClient.searchUsers(any(), any(), anyInt(), anyInt())).thenReturn(searchResponse);

        UserProfile profile = new UserProfile();
        profile.setAuthUserId(authId);
        profile.setFirstName("John");
        when(userProfileRepository.findAllByAuthUserIdIn(any())).thenReturn(List.of(profile));

        mockMvc.perform(get("/api/v1/admin/users")
                .header("X-User-Role", "ROLE_ADMIN")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].firstName").value("John"));
    }

    @Test
    void getUserDetails_ShouldReturnDetails_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        profile.setAuthUserId(id);
        profile.setFirstName("Jane");

        when(userProfileRepository.findByAuthUserId(id)).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/api/v1/admin/users/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("Jane"));
    }

    @Test
    void activateUser_ShouldCallAuthClient_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authServiceClient).updateStatus(id, AccountStatus.ACTIVE);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/activate", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(authServiceClient, times(1)).updateStatus(id, AccountStatus.ACTIVE);
    }

    @Test
    void approveTutor_ShouldUpdateRoleAndStatus_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authServiceClient).updateStatus(id, AccountStatus.ACTIVE);
        doNothing().when(authServiceClient).updateRole(id, Role.ROLE_TUTOR);

        mockMvc.perform(patch("/api/v1/admin/users/{id}/approve-tutor", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(authServiceClient, times(1)).updateStatus(id, AccountStatus.ACTIVE);
        verify(authServiceClient, times(1)).updateRole(id, Role.ROLE_TUTOR);
    }

    @Test
    void deleteUser_ShouldSoftDelete_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(authServiceClient).updateStatus(id, AccountStatus.SUSPENDED);

        mockMvc.perform(delete("/api/v1/admin/users/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
                
        verify(authServiceClient, times(1)).updateStatus(id, AccountStatus.SUSPENDED);
    }
}
