package com.lms.auth.controller;

import com.lms.auth.jwt.JwtUtils;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InternalStatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class InternalStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCredentialRepository userCredentialRepository;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    void getAuthStatistics_ShouldReturnStats() throws Exception {
        when(userCredentialRepository.count()).thenReturn(100L);
        when(userCredentialRepository.countByRole(Role.ROLE_STUDENT)).thenReturn(80L);
        when(userCredentialRepository.countByRole(Role.ROLE_TUTOR)).thenReturn(20L);
        when(userCredentialRepository.countByAccountStatus(AccountStatus.PENDING)).thenReturn(5L);

        mockMvc.perform(get("/internal/statistics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsers").value(100))
                .andExpect(jsonPath("$.totalStudents").value(80))
                .andExpect(jsonPath("$.totalTutors").value(20))
                .andExpect(jsonPath("$.pendingTutorRequests").value(5));
    }
}
