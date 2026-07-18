package com.lms.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.content.dto.request.SectionRequest;
import com.lms.content.entity.Section;
import com.lms.content.service.SectionService;
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

@WebMvcTest(controllers = SectionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SectionService sectionService;

    @Test
    void createSection_ShouldReturnCreated() throws Exception {
        SectionRequest request = new SectionRequest();
        request.setCourseId(UUID.randomUUID());
        request.setTitle("S1");
        request.setDisplayOrder(1);

        doNothing().when(sectionService).createSection(anyString(), anyString(), anyString(), any(SectionRequest.class));

        mockMvc.perform(post("/api/v1/content/sections")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createSection_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        SectionRequest request = new SectionRequest();
        // Missing title, courseId, displayOrder

        mockMvc.perform(post("/api/v1/content/sections")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSection_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        SectionRequest request = new SectionRequest();
        request.setCourseId(UUID.randomUUID());
        request.setTitle("S2");
        request.setDisplayOrder(2);

        doNothing().when(sectionService).updateSection(anyString(), anyString(), anyString(), anyString(), any(SectionRequest.class));

        mockMvc.perform(put("/api/v1/content/sections/{id}", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteSection_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(sectionService).deleteSection(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(delete("/api/v1/content/sections/{id}", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getCourseSections_ShouldReturnList() throws Exception {
        UUID courseId = UUID.randomUUID();
        Section section = new Section();
        section.setId(UUID.randomUUID());
        section.setTitle("S1");
        section.setCourseId(courseId);

        when(sectionService.getCourseSections(courseId.toString())).thenReturn(List.of(section));

        mockMvc.perform(get("/api/v1/content/sections/course/{courseId}", courseId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("S1"));
    }
}
