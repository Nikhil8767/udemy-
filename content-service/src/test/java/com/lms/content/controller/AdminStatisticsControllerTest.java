package com.lms.content.controller;

import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.ResourceRepository;
import com.lms.content.repository.SectionRepository;
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
    private SectionRepository sectionRepository;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private ResourceRepository resourceRepository;

    @Test
    void getStatistics_ShouldReturnStats_WhenRoleIsAdmin() throws Exception {
        when(sectionRepository.count()).thenReturn(50L);
        when(lessonRepository.count()).thenReturn(200L);
        when(resourceRepository.count()).thenReturn(300L);

        mockMvc.perform(get("/api/v1/admin/content/statistics")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSections").value(50))
                .andExpect(jsonPath("$.data.totalLessons").value(200))
                .andExpect(jsonPath("$.data.totalResources").value(300));
    }

    @Test
    void getStatistics_ShouldReturnUnauthorized_WhenRoleIsNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/content/statistics")
                .header("X-User-Role", "ROLE_TUTOR")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()); 
    }
}
