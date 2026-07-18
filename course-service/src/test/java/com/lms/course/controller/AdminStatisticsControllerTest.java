package com.lms.course.controller;

import com.lms.common.enums.CourseStatus;
import com.lms.course.repository.CourseRepository;
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
    private CourseRepository courseRepository;

    @Test
    void getStatistics_ShouldReturnStats_WhenRoleIsAdmin() throws Exception {
        when(courseRepository.count()).thenReturn(100L);
        when(courseRepository.countByCourseStatus(CourseStatus.PUBLISHED)).thenReturn(80L);
        when(courseRepository.countByCourseStatus(CourseStatus.DRAFT)).thenReturn(15L);
        when(courseRepository.countByCourseStatus(CourseStatus.ARCHIVED)).thenReturn(5L);
        when(courseRepository.countByIsFeaturedTrue()).thenReturn(10L);

        mockMvc.perform(get("/api/v1/admin/courses/statistics")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalCourses").value(100))
                .andExpect(jsonPath("$.data.publishedCourses").value(80))
                .andExpect(jsonPath("$.data.draftCourses").value(15))
                .andExpect(jsonPath("$.data.archivedCourses").value(5))
                .andExpect(jsonPath("$.data.featuredCourses").value(10));
    }

    @Test
    void getStatistics_ShouldReturnUnauthorized_WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/courses/statistics")
                .header("X-User-Role", "ROLE_TUTOR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); // In global exception handler this maps correctly, but here we expect basic 401 via test logic if filters active, or 401 manually thrown.
    }
}
