package com.lms.enrollment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.enrollment.dto.request.EnrollmentRequest;
import com.lms.enrollment.dto.request.LessonProgressRequest;
import com.lms.enrollment.entity.Enrollment;
import com.lms.enrollment.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import com.lms.common.handler.GlobalExceptionHandler;
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

@WebMvcTest(controllers = EnrollmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    void enroll_ShouldReturnCreated() throws Exception {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(UUID.randomUUID());

        doNothing().when(enrollmentService).enroll(anyString(), anyString(), any(EnrollmentRequest.class));

        mockMvc.perform(post("/api/v1/enrollments")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getMyEnrollments_ShouldReturnList() throws Exception {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(UUID.randomUUID());
        
        when(enrollmentService.getMyEnrollments(anyString(), anyString())).thenReturn(List.of(enrollment));

        mockMvc.perform(get("/api/v1/enrollments/my")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void markLessonCompleted_ShouldReturnOk() throws Exception {
        UUID courseId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();
        
        LessonProgressRequest request = new LessonProgressRequest();
        request.setWatchTimeMinutes(10);
        request.setLastPositionSeconds(600);

        when(enrollmentService.markLessonCompleted(anyString(), anyString(), anyString(), anyString(), any(LessonProgressRequest.class)))
                .thenReturn(false);

        mockMvc.perform(patch("/api/v1/enrollments/{courseId}/complete-lesson/{lessonId}", courseId, lessonId)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void dropCourse_ShouldReturnOk() throws Exception {
        UUID courseId = UUID.randomUUID();

        doNothing().when(enrollmentService).dropCourse(anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/enrollments/{courseId}/drop", courseId)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
