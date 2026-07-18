package com.lms.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.common.enums.CourseStatus;
import com.lms.course.dto.request.CourseRequest;
import com.lms.course.entity.Category;
import com.lms.course.entity.Course;
import com.lms.course.service.CourseService;
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

@WebMvcTest(controllers = CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    void createCourse_ShouldReturnCreated() throws Exception {
        CourseRequest request = new CourseRequest();
        request.setTitle("Java Programming");
        request.setDescription("Learn Java from scratch");

        doNothing().when(courseService).createCourse(anyString(), anyString(), anyString(), any(CourseRequest.class));

        mockMvc.perform(post("/api/v1/courses")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void createCourse_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        CourseRequest request = new CourseRequest();
        // Missing title, description, etc.

        mockMvc.perform(post("/api/v1/courses")
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateCourse_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        CourseRequest request = new CourseRequest();
        request.setTitle("New Java");

        doNothing().when(courseService).updateCourse(anyString(), anyString(), anyString(), anyString(), any(CourseRequest.class));

        mockMvc.perform(put("/api/v1/courses/{id}", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void publishCourse_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(courseService).publishCourse(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/courses/{id}/publish", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void archiveCourse_ShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(courseService).archiveCourse(anyString(), anyString(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/courses/{id}/archive", id)
                .header("X-User-Id", UUID.randomUUID().toString())
                .header("X-User-Role", "ROLE_TUTOR")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getPublishedCourses_ShouldReturnList() throws Exception {
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Tech");

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Java");
        course.setCategory(category);
        course.setCourseStatus(CourseStatus.PUBLISHED);

        when(courseService.getPublishedCourses()).thenReturn(List.of(course));

        mockMvc.perform(get("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Java"));
    }

    @Test
    void getCourseDetails_ShouldReturnCourse() throws Exception {
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Tech");

        Course course = new Course();
        course.setId(id);
        course.setTitle("Java");
        course.setCategory(category);

        when(courseService.getCourseDetails(anyString(), nullable(String.class), nullable(String.class))).thenReturn(course);

        mockMvc.perform(get("/api/v1/courses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Java"));
    }

    @Test
    void getMyCourses_ShouldReturnList() throws Exception {
        UUID instructorId = UUID.randomUUID();
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Tech");

        Course course = new Course();
        course.setId(UUID.randomUUID());
        course.setTitle("Java");
        course.setCategory(category);

        when(courseService.getMyCourses(instructorId.toString())).thenReturn(List.of(course));

        mockMvc.perform(get("/api/v1/courses/my")
                .header("X-User-Id", instructorId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Java"));
    }

    @Test
    void deleteCourse_ShouldReturnOk_WhenRoleIsAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(courseService).deleteCourse(id.toString(), "111", "ROLE_ADMIN", "ACTIVE");

        mockMvc.perform(delete("/api/v1/courses/{id}", id)
                .header("X-User-Id", "111")
                .header("X-User-Role", "ROLE_ADMIN")
                .header("X-Account-Status", "ACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
