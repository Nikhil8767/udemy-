package com.lms.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.content.dto.request.LessonRequest;
import com.lms.content.entity.Lesson;
import com.lms.content.entity.Section;
import com.lms.content.service.LessonService;
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

@WebMvcTest(controllers = LessonController.class)
@AutoConfigureMockMvc(addFilters = false)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LessonService lessonService;

    @Test
    void createLesson_ShouldReturnCreated() throws Exception {
        LessonRequest request = new LessonRequest();
        request.setSectionId(UUID.randomUUID());
        request.setTitle("L1");
        request.setDisplayOrder(1);

        doNothing().when(lessonService).createLesson(anyString(), anyString(), anyString(), any(LessonRequest.class));

        mockMvc.perform(post("/api/v1/content/lessons")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void updateLesson_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        LessonRequest request = new LessonRequest();
        request.setSectionId(UUID.randomUUID());
        request.setTitle("L2");
        request.setDisplayOrder(2);

        doNothing().when(lessonService).updateLesson(anyString(), anyString(), anyString(), anyString(), any(LessonRequest.class));

        mockMvc.perform(put("/api/v1/content/lessons/{id}", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void deleteLesson_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(lessonService).deleteLesson(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(delete("/api/v1/content/lessons/{id}", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getLesson_ShouldReturnLesson() throws Exception {
        UUID id = UUID.randomUUID();
        Lesson lesson = new Lesson();
        lesson.setId(id);
        lesson.setTitle("L1");
        
        Section section = new Section();
        section.setId(UUID.randomUUID());
        lesson.setSection(section);

        when(lessonService.getLesson(anyString(), nullable(String.class), nullable(String.class))).thenReturn(lesson);

        mockMvc.perform(get("/api/v1/content/lessons/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("L1"));
    }

    @Test
    void getSectionLessons_ShouldReturnList() throws Exception {
        UUID sectionId = UUID.randomUUID();
        Lesson lesson = new Lesson();
        lesson.setId(UUID.randomUUID());
        lesson.setTitle("L1");
        
        Section section = new Section();
        section.setId(sectionId);
        lesson.setSection(section);

        when(lessonService.getSectionLessons(anyString(), nullable(String.class), nullable(String.class))).thenReturn(List.of(lesson));

        mockMvc.perform(get("/api/v1/content/lessons/section/{sectionId}", sectionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("L1"));
    }
}
