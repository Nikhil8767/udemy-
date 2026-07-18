package com.lms.content.service.impl;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.content.client.CourseServiceClient;
import com.lms.content.dto.request.LessonRequest;
import com.lms.content.dto.response.CourseResponse;
import com.lms.content.entity.Lesson;
import com.lms.content.entity.Section;
import com.lms.content.exception.AccessDeniedException;
import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.SectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @InjectMocks
    private LessonServiceImpl lessonService;

    private CourseResponse setupCourseResponse(UUID instructorId) {
        CourseResponse response = new CourseResponse();
        response.setInstructorId(instructorId);
        return response;
    }

    private ApiResponse<CourseResponse> setupApiResponse(UUID instructorId) {
        ApiResponse<CourseResponse> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(setupCourseResponse(instructorId));
        return apiResponse;
    }

    @Test
    void createLesson_ShouldSave_WhenTutorIsOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        LessonRequest request = new LessonRequest();
        request.setSectionId(sectionId);
        request.setTitle("L1");
        request.setDisplayOrder(1);

        Section section = Section.builder().id(sectionId).courseId(courseId).build();

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));
        when(lessonRepository.existsBySectionIdAndDisplayOrder(sectionId, 1)).thenReturn(false);

        lessonService.createLesson(instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request);

        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void createLesson_ShouldThrowAccessDenied_WhenNotTutor() {
        LessonRequest request = new LessonRequest();
        
        assertThrows(AccessDeniedException.class, () -> 
            lessonService.createLesson(UUID.randomUUID().toString(), "ROLE_STUDENT", "ACTIVE", request)
        );
    }

    @Test
    void updateLesson_ShouldUpdate_WhenValidOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        Section section = Section.builder().id(sectionId).courseId(courseId).build();
        Lesson lesson = Lesson.builder().id(lessonId).section(section).displayOrder(1).build();

        LessonRequest request = new LessonRequest();
        request.setDisplayOrder(2);

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));
        when(lessonRepository.existsBySectionIdAndDisplayOrder(sectionId, 2)).thenReturn(false);

        lessonService.updateLesson(lessonId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request);

        verify(lessonRepository, times(1)).save(lesson);
        assertThat(lesson.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void deleteLesson_ShouldDelete() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        Section section = Section.builder().id(sectionId).courseId(courseId).build();
        Lesson lesson = Lesson.builder().id(lessonId).section(section).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));

        lessonService.deleteLesson(lessonId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE");

        verify(lessonRepository, times(1)).delete(lesson);
    }

    @Test
    void getLesson_ShouldReturnLesson() {
        UUID lessonId = UUID.randomUUID();
        Lesson lesson = new Lesson();
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        Lesson result = lessonService.getLesson(lessonId.toString(), null, null);

        assertThat(result).isNotNull();
    }
}
