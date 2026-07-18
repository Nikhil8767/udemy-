package com.lms.enrollment.controller;

import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.lms.common.handler.GlobalExceptionHandler;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminEnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AdminEnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentRepository enrollmentRepository;

    @Test
    void listEnrollments_ShouldReturnPaginatedList_WhenRoleIsAdmin() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(UUID.randomUUID());

        when(enrollmentRepository.searchEnrollments(any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(enrollment)));

        mockMvc.perform(get("/api/v1/admin/enrollments")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void listEnrollments_ShouldReturnUnauthorized_WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/enrollments")
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
