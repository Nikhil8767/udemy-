package com.lms.content.service.impl;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.content.client.CourseServiceClient;
import com.lms.content.dto.request.SectionRequest;
import com.lms.content.dto.response.CourseResponse;
import com.lms.content.entity.Section;
import com.lms.content.exception.AccessDeniedException;
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
class SectionServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseServiceClient courseServiceClient;

    @InjectMocks
    private SectionServiceImpl sectionService;

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
    void createSection_ShouldSave_WhenTutorIsOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        SectionRequest request = new SectionRequest();
        request.setCourseId(courseId);
        request.setTitle("S1");
        request.setDisplayOrder(1);

        when(courseServiceClient.getCourseDetails(courseId.toString(), instructorId.toString(), "ROLE_TUTOR"))
                .thenReturn(setupApiResponse(instructorId));
        
        when(sectionRepository.existsByCourseIdAndDisplayOrder(courseId, 1)).thenReturn(false);

        sectionService.createSection(instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request);

        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void createSection_ShouldThrowAccessDenied_WhenNotTutor() {
        SectionRequest request = new SectionRequest();
        
        assertThrows(AccessDeniedException.class, () -> 
            sectionService.createSection(UUID.randomUUID().toString(), "ROLE_STUDENT", "ACTIVE", request)
        );
    }

    @Test
    void createSection_ShouldThrowAccessDenied_WhenNotCourseOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        SectionRequest request = new SectionRequest();
        request.setCourseId(courseId);
        
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(UUID.randomUUID())); // Different owner

        assertThrows(AccessDeniedException.class, () -> 
            sectionService.createSection(instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request)
        );
    }

    @Test
    void createSection_ShouldSave_WhenAdmin() {
        UUID courseId = UUID.randomUUID();

        SectionRequest request = new SectionRequest();
        request.setCourseId(courseId);
        request.setDisplayOrder(1);
        
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(UUID.randomUUID()));
        
        when(sectionRepository.existsByCourseIdAndDisplayOrder(courseId, 1)).thenReturn(false);

        sectionService.createSection(UUID.randomUUID().toString(), "ROLE_ADMIN", "ACTIVE", request);

        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void updateSection_ShouldUpdate_WhenValidOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        Section section = Section.builder().courseId(courseId).displayOrder(1).build();

        SectionRequest request = new SectionRequest();
        request.setDisplayOrder(2);

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));
        when(sectionRepository.existsByCourseIdAndDisplayOrder(courseId, 2)).thenReturn(false);

        sectionService.updateSection(sectionId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request);

        verify(sectionRepository, times(1)).save(section);
        assertThat(section.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void updateSection_ShouldThrowDuplicate_WhenDisplayOrderExists() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        Section section = Section.builder().courseId(courseId).displayOrder(1).build();

        SectionRequest request = new SectionRequest();
        request.setDisplayOrder(2);

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));
        when(sectionRepository.existsByCourseIdAndDisplayOrder(courseId, 2)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> 
            sectionService.updateSection(sectionId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE", request)
        );
    }

    @Test
    void deleteSection_ShouldDelete() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        Section section = Section.builder().courseId(courseId).build();

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(courseServiceClient.getCourseDetails(anyString(), anyString(), anyString()))
                .thenReturn(setupApiResponse(instructorId));

        sectionService.deleteSection(sectionId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE");

        verify(sectionRepository, times(1)).delete(section);
    }

    @Test
    void getCourseSections_ShouldReturnList() {
        UUID courseId = UUID.randomUUID();
        when(sectionRepository.findAllByCourseIdOrderByDisplayOrderAsc(courseId)).thenReturn(List.of(new Section()));

        List<Section> results = sectionService.getCourseSections(courseId.toString());

        assertThat(results).hasSize(1);
    }
}
