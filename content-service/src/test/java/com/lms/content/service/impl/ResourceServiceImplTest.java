package com.lms.content.service.impl;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.content.client.CourseServiceClient;
import com.lms.content.dto.request.ResourceRequest;
import com.lms.content.dto.response.CourseResponse;
import com.lms.content.entity.Lesson;
import com.lms.content.entity.Resource;
import com.lms.content.entity.Section;
import com.lms.content.exception.AccessDeniedException;
import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.ResourceRepository;
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
class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @InjectMocks
    private ResourceServiceImpl resourceService;

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
    void createResource_ShouldSave_WhenTutorIsOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID lessonId = UUID.randomUUID();

        ResourceRequest request = new ResourceRequest();
        request.setLessonId(lessonId);
        request.setTitle("Doc");

        Section section = Section.builder().id(sectionId).courseId(courseId).build();
        Lesson lesson = Lesson.builder().id(lessonId).section(section).build();

        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));

        resourceService.createResource(instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request);

        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void createResource_ShouldThrowAccessDenied_WhenNotTutor() {
        ResourceRequest request = new ResourceRequest();
        
        assertThrows(AccessDeniedException.class, () -> 
            resourceService.createResource(UUID.randomUUID().toString(), "ROLE_STUDENT", "ACTIVE", request)
        );
    }

    @Test
    void getLessonResources_ShouldReturnList() {
        UUID lessonId = UUID.randomUUID();
        when(resourceRepository.findAllByLessonId(lessonId)).thenReturn(List.of(new Resource()));

        List<Resource> results = resourceService.getLessonResources(lessonId.toString(), null, null);

        assertThat(results).hasSize(1);
    }
}
