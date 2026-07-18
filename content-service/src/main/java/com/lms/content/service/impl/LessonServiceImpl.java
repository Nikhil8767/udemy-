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
import com.lms.content.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
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
    public void createLesson(String userId, String role, String accountStatus, LessonRequest request) {
        verifyTutorAccess(role, accountStatus);

        Section section = sectionRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found."));

        verifyCourseOwnership(section.getCourseId().toString(), userId, role);

        Integer finalDisplayOrder = request.getDisplayOrder();
        if (finalDisplayOrder == null || lessonRepository.existsBySectionIdAndDisplayOrder(section.getId(), finalDisplayOrder)) {
            Integer maxOrder = lessonRepository.findMaxDisplayOrderBySectionId(section.getId());
            finalDisplayOrder = (maxOrder == null ? 0 : maxOrder) + 1;
        }

        Lesson lesson = Lesson.builder()
                .section(section)
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(request.getContentType())
                .videoUrl(request.getVideoUrl())
                .pdfUrl(request.getPdfUrl())
                .articleContent(request.getArticleContent())
                .durationMinutes(request.getDurationMinutes())
                .isPreview(request.isPreview())
                .displayOrder(finalDisplayOrder)
                .build();
        lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public void updateLesson(String lessonId, String userId, String role, String accountStatus, LessonRequest request) {
        verifyTutorAccess(role, accountStatus);
        
        Lesson lesson = lessonRepository.findById(UUID.fromString(lessonId))
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

        verifyCourseOwnership(lesson.getSection().getCourseId().toString(), userId, role);

        Integer finalDisplayOrder = request.getDisplayOrder();
        if (!lesson.getDisplayOrder().equals(finalDisplayOrder) && 
            (finalDisplayOrder == null || lessonRepository.existsBySectionIdAndDisplayOrder(lesson.getSection().getId(), finalDisplayOrder))) {
            Integer maxOrder = lessonRepository.findMaxDisplayOrderBySectionId(lesson.getSection().getId());
            finalDisplayOrder = (maxOrder == null ? 0 : maxOrder) + 1;
        }

        lesson.setTitle(request.getTitle());
        lesson.setDescription(request.getDescription());
        lesson.setContentType(request.getContentType());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setPdfUrl(request.getPdfUrl());
        lesson.setArticleContent(request.getArticleContent());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setPreview(request.isPreview());
        lesson.setDisplayOrder(finalDisplayOrder);
        lessonRepository.save(lesson);
    }

    @Override
    @Transactional
    public void deleteLesson(String lessonId, String userId, String role, String accountStatus) {
        verifyTutorAccess(role, accountStatus);
        
        Lesson lesson = lessonRepository.findById(UUID.fromString(lessonId))
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

        verifyCourseOwnership(lesson.getSection().getCourseId().toString(), userId, role);
        
        lessonRepository.delete(lesson);
    }

    @Override
    @Transactional(readOnly = true)
    public Lesson getLesson(String lessonId, String userId, String role) {
        Lesson lesson = lessonRepository.findById(UUID.fromString(lessonId))
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));
        return lesson;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lesson> getSectionLessons(String sectionId, String userId, String role) {
        return lessonRepository.findAllBySectionIdOrderByDisplayOrderAsc(UUID.fromString(sectionId));
    }
}
