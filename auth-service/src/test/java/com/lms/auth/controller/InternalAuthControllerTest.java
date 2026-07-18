package com.lms.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.jwt.JwtUtils;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class InternalAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCredentialRepository userCredentialRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void searchUsers_ShouldReturnPaginatedIds() throws Exception {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Page<UUID> mockPage = new PageImpl<>(List.of(id1, id2), PageRequest.of(0, 10), 2);

        when(userCredentialRepository.findIdsByRoleAndStatus(any(), any(), any())).thenReturn(mockPage);

        mockMvc.perform(get("/internal/users/search")
                .param("role", "ROLE_STUDENT")
                .param("status", "ACTIVE")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userIds.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void updateStatus_ShouldChangeStatus_WhenUserExists() throws Exception {
        UUID id = UUID.randomUUID();
        UserCredential user = new UserCredential();
        user.setId(id);
        user.setAccountStatus(AccountStatus.PENDING);

        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/internal/users/{id}/status", id)
                .param("status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userCredentialRepository, times(1)).save(user);
        assert user.getAccountStatus() == AccountStatus.ACTIVE;
    }

    @Test
    void updateRole_ShouldChangeRole_WhenUserExists() throws Exception {
        UUID id = UUID.randomUUID();
        UserCredential user = new UserCredential();
        user.setId(id);
        user.setRole(Role.ROLE_STUDENT);

        when(userCredentialRepository.findById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(patch("/internal/users/{id}/role", id)
                .param("role", "ROLE_TUTOR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userCredentialRepository, times(1)).save(user);
        assert user.getRole() == Role.ROLE_TUTOR;
    }
}
