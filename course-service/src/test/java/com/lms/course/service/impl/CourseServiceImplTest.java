package com.lms.course.service.impl;

import com.lms.common.enums.CourseLevel;
import com.lms.common.enums.CourseStatus;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.course.dto.request.CourseRequest;
import com.lms.course.entity.Category;
import com.lms.course.entity.Course;
import com.lms.course.exception.AccessDeniedException;
import com.lms.course.repository.CategoryRepository;
import com.lms.course.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void createCourse_ShouldSaveCourse_WhenValidTutor() {
        CourseRequest req = new CourseRequest();
        req.setTitle("Java Course");
        req.setCategoryId(UUID.randomUUID());
        req.setCourseLevel(CourseLevel.BEGINNER);
        req.setPrice(BigDecimal.TEN);
        req.setEstimatedDurationMinutes(120);

        when(courseRepository.existsByTitle("Java Course")).thenReturn(false);
        when(categoryRepository.findById(req.getCategoryId())).thenReturn(Optional.of(new Category()));

        courseService.createCourse(UUID.randomUUID().toString(), "ROLE_TUTOR", "ACTIVE", req);

        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void createCourse_ShouldThrowAccessDenied_WhenNotActiveTutor() {
        CourseRequest req = new CourseRequest();
        
        assertThrows(AccessDeniedException.class, () -> 
            courseService.createCourse(UUID.randomUUID().toString(), "ROLE_TUTOR", "PENDING", req)
        );
    }

    @Test
    void createCourse_ShouldThrowDuplicateResource_WhenTitleExists() {
        CourseRequest req = new CourseRequest();
        req.setTitle("Java");
        
        when(courseRepository.existsByTitle("Java")).thenReturn(true);
        
        assertThrows(DuplicateResourceException.class, () -> 
            courseService.createCourse(UUID.randomUUID().toString(), "ROLE_ADMIN", "ACTIVE", req)
        );
    }

    @Test
    void updateCourse_ShouldUpdate_WhenOwnerIsTutor() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Course course = new Course();
        course.setId(courseId);
        course.setInstructorId(instructorId);
        course.setTitle("Old Title");

        CourseRequest req = new CourseRequest();
        req.setTitle("New Title");
        req.setCategoryId(categoryId);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(courseRepository.existsByTitle("New Title")).thenReturn(false);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(new Category()));

        courseService.updateCourse(courseId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE", req);

        assertThat(course.getTitle()).isEqualTo("New Title");
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void publishCourse_ShouldChangeStatus_WhenValidAdmin() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setCourseStatus(CourseStatus.DRAFT);
        
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.publishCourse(courseId.toString(), UUID.randomUUID().toString(), "ROLE_ADMIN", "ACTIVE");

        assertThat(course.getCourseStatus()).isEqualTo(CourseStatus.PUBLISHED);
        assertThat(course.getPublishedAt()).isNotNull();
    }

    @Test
    void archiveCourse_ShouldChangeStatus_WhenValidOwner() {
        UUID instructorId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setInstructorId(instructorId);
        course.setCourseStatus(CourseStatus.PUBLISHED);
        
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.archiveCourse(courseId.toString(), instructorId.toString(), "ROLE_TUTOR", "ACTIVE");

        assertThat(course.getCourseStatus()).isEqualTo(CourseStatus.ARCHIVED);
    }

    @Test
    void getCourseDetails_ShouldReturnCourse_WhenPublished() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setCourseStatus(CourseStatus.PUBLISHED);
        
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseDetails(courseId.toString(), null, null);
        
        assertThat(result).isNotNull();
    }

    @Test
    void getCourseDetails_ShouldThrowNotFound_WhenDraftAndNotOwner() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setCourseStatus(CourseStatus.DRAFT);
        course.setInstructorId(UUID.randomUUID());
        
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(ResourceNotFoundException.class, () -> 
            courseService.getCourseDetails(courseId.toString(), UUID.randomUUID().toString(), "ROLE_STUDENT")
        );
    }

    @Test
    void deleteCourse_ShouldDelete_WhenRoleIsAdmin() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        courseService.deleteCourse(courseId.toString(), UUID.randomUUID().toString(), "ROLE_ADMIN", "ACTIVE");

        verify(courseRepository, times(1)).delete(course);
    }

    @Test
    void deleteCourse_ShouldThrowAccessDenied_WhenNotOwner() {
        UUID courseId = UUID.randomUUID();
        Course course = new Course();
        course.setInstructorId(UUID.randomUUID());
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assertThrows(AccessDeniedException.class, () -> 
            courseService.deleteCourse(courseId.toString(), UUID.randomUUID().toString(), "ROLE_TUTOR", "ACTIVE")
        );
    }
}
