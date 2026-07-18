package com.lms.enrollment.controller;

import com.lms.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.lms.common.handler.GlobalExceptionHandler;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminStatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @Test
    void getStatistics_ShouldReturnStats_WhenRoleIsAdmin() throws Exception {
        when(enrollmentRepository.count()).thenReturn(100L);
        when(enrollmentRepository.getAverageCompletionPercentage()).thenReturn(45.5);

        mockMvc.perform(get("/api/v1/admin/enrollments/statistics")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalEnrollments").value(100))
                .andExpect(jsonPath("$.data.averageCompletionPercentage").value(45.5));
    }

    @Test
    void getStatistics_ShouldReturnUnauthorized_WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/enrollments/statistics")
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
