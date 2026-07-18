package com.lms.content.service.impl;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.content.client.CourseServiceClient;
import com.lms.content.dto.request.ResourceRequest;
import com.lms.content.dto.response.CourseResponse;
import com.lms.content.entity.Lesson;
import com.lms.content.entity.Resource;
import com.lms.content.exception.AccessDeniedException;
import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.ResourceRepository;
import com.lms.content.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final LessonRepository lessonRepository;
    private final CourseServiceClient courseServiceClient;

    private void verifyTutorAccess(String role, String accountStatus) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            if (!"ROLE_TUTOR".equalsIgnoreCase(role) || !"ACTIVE".equalsIgnoreCase(accountStatus)) {
                throw new AccessDeniedException("Only active tutors may modify content.");
            }
        }
    }

    private void verifyCourseOwnership(String courseId, String userId, String role) {
        try {
            ApiResponse<CourseResponse> courseResponse = courseServiceClient.getCourseDetails(courseId, userId, role);
            if (courseResponse == null || !courseResponse.isSuccess() || courseResponse.getData() == null) {
                throw new ResourceNotFoundException("Course not found.");
            }
            if (!"ROLE_ADMIN".equalsIgnoreCase(role) && !courseResponse.getData().getInstructorId().toString().equals(userId)) {
                throw new AccessDeniedException("Access denied.");
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("Course not found.");
        }
    }

    @Override
    @Transactional
    public void createResource(String userId, String role, String accountStatus, ResourceRequest request) {
        verifyTutorAccess(role, accountStatus);

        Lesson lesson = lessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

        verifyCourseOwnership(lesson.getSection().getCourseId().toString(), userId, role);

        Resource resource = Resource.builder()
                .lesson(lesson)
                .title(request.getTitle())
                .resourceType(request.getResourceType())
                .fileUrl(request.getFileUrl())
                .build();
        
        resourceRepository.save(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resource> getLessonResources(String lessonId, String userId, String role) {
        return resourceRepository.findAllByLessonId(UUID.fromString(lessonId));
    }

    @Override
    @Transactional
    public void updateResource(String id, String userId, String role, String accountStatus, ResourceRequest request) {
        verifyTutorAccess(role, accountStatus);

        Resource resource = resourceRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));

        verifyCourseOwnership(resource.getLesson().getSection().getCourseId().toString(), userId, role);

        resource.setTitle(request.getTitle());
        resource.setResourceType(request.getResourceType());
        resource.setFileUrl(request.getFileUrl());
        
        resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public void deleteResource(String id, String userId, String role, String accountStatus) {
        verifyTutorAccess(role, accountStatus);

        Resource resource = resourceRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found."));

        verifyCourseOwnership(resource.getLesson().getSection().getCourseId().toString(), userId, role);

        resourceRepository.delete(resource);
    }
}
