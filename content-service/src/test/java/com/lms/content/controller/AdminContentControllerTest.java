package com.lms.content.controller;

import com.lms.content.entity.Lesson;
import com.lms.content.entity.Resource;
import com.lms.content.entity.Section;
import com.lms.content.enums.LessonContentType;
import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.ResourceRepository;
import com.lms.content.repository.SectionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminContentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionRepository sectionRepository;

    @MockBean
    private LessonRepository lessonRepository;

    @MockBean
    private ResourceRepository resourceRepository;

    @Test
    void listSections_ShouldReturnPaginatedList_WhenRoleIsAdmin() throws Exception {
        Section section = new Section();
        section.setId(UUID.randomUUID());
        section.setTitle("S1");
        section.setCourseId(UUID.randomUUID());

        when(sectionRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(section)));

        mockMvc.perform(get("/api/v1/admin/sections")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("S1"));
    }

    @Test
    void listLessons_ShouldReturnPaginatedList_WhenRoleIsAdmin() throws Exception {
        Lesson lesson = new Lesson();
        lesson.setId(UUID.randomUUID());
        lesson.setTitle("L1");
        lesson.setContentType(LessonContentType.VIDEO);
        lesson.setDisplayOrder(1);

        when(lessonRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(lesson)));

        mockMvc.perform(get("/api/v1/admin/lessons")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("L1"));
    }

    @Test
    void deleteLesson_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        Lesson lesson = new Lesson();
        lesson.setId(id);
        
        when(lessonRepository.findById(id)).thenReturn(Optional.of(lesson));

        mockMvc.perform(delete("/api/v1/admin/lessons/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(lessonRepository, times(1)).delete(lesson);
    }

    @Test
    void deleteResource_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(id);
        
        when(resourceRepository.findById(id)).thenReturn(Optional.of(resource));

        mockMvc.perform(delete("/api/v1/admin/resources/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(resourceRepository, times(1)).delete(resource);
    }
}
