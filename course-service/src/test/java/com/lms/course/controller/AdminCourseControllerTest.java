package com.lms.course.controller;

import com.lms.common.enums.CourseStatus;
import com.lms.course.entity.Course;
import com.lms.course.repository.CourseRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRepository courseRepository;

    @Test
    void listCourses_ShouldReturnPaginatedList_WhenRoleIsAdmin() throws Exception {
        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Java");
        course.setCourseStatus(CourseStatus.PUBLISHED);

        when(courseRepository.searchCourses(any(), any(), any(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(course)));

        mockMvc.perform(get("/api/v1/admin/courses")
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Java"));
    }

    @Test
    void publishCourse_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        Course course = new Course();
        course.setId(id);
        
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        mockMvc.perform(patch("/api/v1/admin/courses/{id}/publish", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(courseRepository, times(1)).save(course);
        assert course.getCourseStatus() == CourseStatus.PUBLISHED;
    }

    @Test
    void archiveCourse_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        Course course = new Course();
        course.setId(id);
        
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        mockMvc.perform(patch("/api/v1/admin/courses/{id}/archive", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(courseRepository, times(1)).save(course);
        assert course.getCourseStatus() == CourseStatus.ARCHIVED;
    }

    @Test
    void featureCourse_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        Course course = new Course();
        course.setId(id);
        course.setFeatured(false);
        
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        mockMvc.perform(patch("/api/v1/admin/courses/{id}/feature", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(courseRepository, times(1)).save(course);
        assert course.isFeatured();
    }

    @Test
    void deleteCourse_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        Course course = new Course();
        course.setId(id);
        
        when(courseRepository.findById(id)).thenReturn(Optional.of(course));

        mockMvc.perform(delete("/api/v1/admin/courses/{id}", id)
                .header("X-User-Role", "ROLE_ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
        
        verify(courseRepository, times(1)).delete(course);
    }
}
