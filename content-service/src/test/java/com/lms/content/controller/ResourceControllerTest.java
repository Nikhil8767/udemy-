package com.lms.content.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.content.dto.request.ResourceRequest;
import com.lms.content.entity.Lesson;
import com.lms.content.entity.Resource;
import com.lms.content.service.ResourceService;
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

@WebMvcTest(controllers = ResourceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    @Test
    void createResource_ShouldReturnCreated() throws Exception {
        ResourceRequest request = new ResourceRequest();
        request.setLessonId(UUID.randomUUID());
        request.setTitle("Doc");
        request.setResourceType("pdf");
        request.setFileUrl("http://example.com/doc.pdf");

        doNothing().when(resourceService).createResource(anyString(), anyString(), anyString(), any(ResourceRequest.class));

        mockMvc.perform(post("/api/v1/content/resources")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getLessonResources_ShouldReturnList() throws Exception {
        UUID lessonId = UUID.randomUUID();
        
        Lesson lesson = new Lesson();
        lesson.setId(lessonId);

        Resource resource = new Resource();
        resource.setId(UUID.randomUUID());
        resource.setTitle("Doc");
        resource.setLesson(lesson);

        when(resourceService.getLessonResources(anyString(), nullable(String.class), nullable(String.class)))
                .thenReturn(List.of(resource));

        mockMvc.perform(get("/api/v1/content/resources/lesson/{lessonId}", lessonId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Doc"));
    }
}
