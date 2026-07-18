package com.lms.user.controller;

import com.lms.user.client.AuthServiceClient;
import com.lms.user.dto.response.AuthStatisticsResponse;
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

@WebMvcTest(controllers = AdminStatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthServiceClient authServiceClient;

    @Test
    void getStatistics_ShouldReturnStats_WhenRoleIsAdmin() throws Exception {
        AuthStatisticsResponse stats = new AuthStatisticsResponse();
        stats.setTotalUsers(100);
        stats.setTotalStudents(80);

        when(authServiceClient.getAuthStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/users/statistics")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalUsers").value(100))
                .andExpect(jsonPath("$.data.totalStudents").value(80));
    }

    @Test
    void getStatistics_ShouldReturnUnauthorized_WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users/statistics")
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
