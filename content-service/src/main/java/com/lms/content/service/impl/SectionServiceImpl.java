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
import com.lms.content.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

    private final SectionRepository sectionRepository;
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
    public void createSection(String userId, String role, String accountStatus, SectionRequest request) {
        verifyTutorAccess(role, accountStatus);
        verifyCourseOwnership(request.getCourseId().toString(), userId, role);

        Integer maxOrder = sectionRepository.findMaxDisplayOrderByCourseId(request.getCourseId());
        Integer finalDisplayOrder = (maxOrder == null ? 0 : maxOrder) + 1;

        Section section = Section.builder()
                .courseId(request.getCourseId())
                .title(request.getTitle())
                .description(request.getDescription())
                .displayOrder(finalDisplayOrder)
                .build();
        sectionRepository.save(section);
    }

    @Override
    @Transactional
    public void updateSection(String sectionId, String userId, String role, String accountStatus, SectionRequest request) {
        verifyTutorAccess(role, accountStatus);
        
        Section section = sectionRepository.findById(UUID.fromString(sectionId))
                .orElseThrow(() -> new ResourceNotFoundException("Section not found."));

        verifyCourseOwnership(section.getCourseId().toString(), userId, role);

        Integer finalDisplayOrder = request.getDisplayOrder();
        if (!section.getDisplayOrder().equals(finalDisplayOrder) && 
            (finalDisplayOrder == null || sectionRepository.existsByCourseIdAndDisplayOrder(section.getCourseId(), finalDisplayOrder))) {
            Integer maxOrder = sectionRepository.findMaxDisplayOrderByCourseId(section.getCourseId());
            finalDisplayOrder = (maxOrder == null ? 0 : maxOrder) + 1;
        }

        section.setTitle(request.getTitle());
        section.setDescription(request.getDescription());
        section.setDisplayOrder(finalDisplayOrder);
        sectionRepository.save(section);
    }

    @Override
    @Transactional
    public void deleteSection(String sectionId, String userId, String role, String accountStatus) {
        verifyTutorAccess(role, accountStatus);
        
        Section section = sectionRepository.findById(UUID.fromString(sectionId))
                .orElseThrow(() -> new ResourceNotFoundException("Section not found."));

        verifyCourseOwnership(section.getCourseId().toString(), userId, role);
        
        sectionRepository.delete(section);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> getCourseSections(String courseId) {
        return sectionRepository.findAllByCourseIdOrderByDisplayOrderAsc(UUID.fromString(courseId));
    }
}
