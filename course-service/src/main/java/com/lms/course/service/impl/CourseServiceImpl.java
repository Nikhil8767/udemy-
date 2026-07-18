package com.lms.course.service.impl;

import com.lms.common.enums.CourseStatus;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.course.exception.AccessDeniedException;
import com.lms.course.dto.request.CourseRequest;
import com.lms.course.entity.Category;
import com.lms.course.entity.Course;
import com.lms.course.repository.CategoryRepository;
import com.lms.course.repository.CourseRepository;
import com.lms.course.service.CourseService;
import com.lms.course.client.UserServiceClient;
import com.lms.course.client.ContentServiceClient;
import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.BusinessException;
import com.lms.course.dto.response.ProfileResponse;
import com.lms.course.dto.response.SectionResponse;
import com.lms.course.dto.response.LessonResponse;
import com.lms.course.dto.response.ValidationReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserServiceClient userServiceClient;
    private final ContentServiceClient contentServiceClient;

    @Override
    @Transactional
    public void createCourse(String userId, String role, String accountStatus, CourseRequest request) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            if (!"ROLE_TUTOR".equalsIgnoreCase(role) || !"ACTIVE".equalsIgnoreCase(accountStatus)) {
                throw new AccessDeniedException("Only active tutors can create courses.");
            }
        }

        if (courseRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Duplicate course title.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        Course course = Course.builder()
                .title(request.getTitle())
                .subtitle(request.getSubtitle())
                .description(request.getDescription())
                .shortDescription(request.getShortDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .bannerUrl(request.getBannerUrl())
                .language(request.getLanguage())
                .courseLevel(request.getCourseLevel())
                .courseStatus(CourseStatus.DRAFT)
                .category(category)
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .currency(request.getCurrency())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .instructorId(UUID.fromString(userId))
                .createdBy(UUID.fromString(userId))
                .updatedBy(UUID.fromString(userId))
                .build();
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void updateCourse(String courseId, String userId, String role, String accountStatus, CourseRequest request) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            if (!"ROLE_TUTOR".equalsIgnoreCase(role) || !"ACTIVE".equalsIgnoreCase(accountStatus)) {
                throw new AccessDeniedException("Access denied.");
            }
            if (!course.getInstructorId().toString().equals(userId)) {
                throw new AccessDeniedException("Access denied.");
            }
        }

        if (!course.getTitle().equals(request.getTitle()) && courseRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Duplicate course title.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found."));

        course.setTitle(request.getTitle());
        course.setSubtitle(request.getSubtitle());
        course.setDescription(request.getDescription());
        course.setShortDescription(request.getShortDescription());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setBannerUrl(request.getBannerUrl());
        course.setLanguage(request.getLanguage());
        course.setCourseLevel(request.getCourseLevel());
        course.setCategory(category);
        course.setPrice(request.getPrice());
        course.setDiscountPrice(request.getDiscountPrice());
        course.setCurrency(request.getCurrency());
        course.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        course.setUpdatedBy(UUID.fromString(userId));

        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void publishCourse(String courseId, String userId, String role, String accountStatus) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        checkCourseAccess(course, userId, role, accountStatus);
        ValidationReportResponse report = validateCourse(courseId, userId, role);
        if (!report.isReadyToPublish()) {
            throw new BusinessException(String.join("|", report.getErrors()));
        }

        course.setCourseStatus(CourseStatus.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());
        course.setUpdatedBy(UUID.fromString(userId));
        courseRepository.save(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ValidationReportResponse validateCourse(String courseId, String userId, String role) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        ValidationReportResponse report = new ValidationReportResponse();
        List<String> errors = new ArrayList<>();
        
        // 1. Profile Completion
        try {
            ApiResponse<ProfileResponse> profileRes = userServiceClient.getMyProfile(userId);
            if (profileRes != null && profileRes.isSuccess() && profileRes.getData() != null) {
                if (profileRes.getData().getCompletionPercentage() != null && profileRes.getData().getCompletionPercentage() >= 100) {
                    report.setProfileComplete(true);
                } else {
                    errors.add("Your tutor profile must be 100% complete before publishing a course.");
                }
            } else {
                errors.add("Could not fetch tutor profile for validation.");
            }
        } catch (Exception e) {
            errors.add("Tutor profile validation failed (Service unavailable).");
        }
        
        // 2. Course Info Check
        report.setCourseInfoComplete(true);
        if (course.getThumbnailUrl() != null && !course.getThumbnailUrl().isEmpty()) {
            report.setThumbnailSet(true);
        } else {
            errors.add("Course thumbnail is required.");
            report.setCourseInfoComplete(false);
        }
        
        if (course.getBannerUrl() != null && !course.getBannerUrl().isEmpty()) {
            report.setBannerSet(true);
        } else {
            errors.add("Course banner is required.");
            report.setCourseInfoComplete(false);
        }
        
        if (course.getCategory() != null) {
            report.setCategorySet(true);
        } else {
            errors.add("Course category is required.");
            report.setCourseInfoComplete(false);
        }
        
        if (course.getCourseLevel() != null) {
            report.setCourseLevelSet(true);
        } else {
            errors.add("Course level is required.");
            report.setCourseInfoComplete(false);
        }
        
        // 3. Sections & Lessons Check
        boolean allSectionsHaveLessons = true;
        boolean allLessonsHaveResources = true;
        boolean hasSections = false;
        
        try {
            ApiResponse<List<SectionResponse>> sectionsRes = contentServiceClient.getCourseSections(course.getId().toString());
            if (sectionsRes != null && sectionsRes.isSuccess() && sectionsRes.getData() != null && !sectionsRes.getData().isEmpty()) {
                hasSections = true;
                report.setHasSections(true);
                
                for (SectionResponse section : sectionsRes.getData()) {
                    try {
                        ApiResponse<List<LessonResponse>> lessonsRes = contentServiceClient.getSectionLessons(section.getId().toString(), userId, role);
                        if (lessonsRes == null || !lessonsRes.isSuccess() || lessonsRes.getData() == null || lessonsRes.getData().isEmpty()) {
                            errors.add("Section '" + section.getTitle() + "' must have at least one lesson.");
                            allSectionsHaveLessons = false;
                        } else {
                            for (LessonResponse lesson : lessonsRes.getData()) {
                                try {
                                    ApiResponse<List<com.lms.course.dto.response.ResourceResponse>> resourcesRes = contentServiceClient.getLessonResources(lesson.getId().toString(), userId, role);
                                    if (resourcesRes == null || !resourcesRes.isSuccess() || resourcesRes.getData() == null || resourcesRes.getData().isEmpty()) {
                                        errors.add("Lesson '" + lesson.getTitle() + "' must have at least one resource.");
                                        allLessonsHaveResources = false;
                                    }
                                } catch (Exception e) {
                                    errors.add("Could not validate resources for lesson '" + lesson.getTitle() + "'.");
                                    allLessonsHaveResources = false;
                                }
                            }
                        }
                    } catch (Exception e) {
                        errors.add("Could not validate lessons for section '" + section.getTitle() + "'.");
                        allSectionsHaveLessons = false;
                    }
                }
            } else {
                errors.add("Course must have at least one section.");
            }
        } catch (Exception e) {
            errors.add("Could not fetch course sections for validation.");
        }
        
        report.setAllSectionsHaveLessons(allSectionsHaveLessons && hasSections);
        report.setAllLessonsHaveResources(allLessonsHaveResources && hasSections);
        
        report.setReadyToPublish(errors.isEmpty());
        report.setErrors(errors);
        return report;
    }

    @Override
    @Transactional
    public void archiveCourse(String courseId, String userId, String role, String accountStatus) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        checkCourseAccess(course, userId, role, accountStatus);

        course.setCourseStatus(CourseStatus.ARCHIVED);
        course.setUpdatedBy(UUID.fromString(userId));
        courseRepository.save(course);
    }

    private void checkCourseAccess(Course course, String userId, String role, String accountStatus) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            if (!"ROLE_TUTOR".equalsIgnoreCase(role) || !"ACTIVE".equalsIgnoreCase(accountStatus)) {
                throw new AccessDeniedException("Access denied.");
            }
            if (!course.getInstructorId().toString().equals(userId)) {
                throw new AccessDeniedException("Access denied.");
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getPublishedCourses() {
        return courseRepository.findAllByCourseStatus(CourseStatus.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public Course getCourseDetails(String courseId, String userId, String role) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if ("ROLE_ADMIN".equalsIgnoreCase(role) || (userId != null && course.getInstructorId().toString().equals(userId))) {
            return course;
        }

        if (course.getCourseStatus() != CourseStatus.PUBLISHED) {
            throw new ResourceNotFoundException("Course not found.");
        }
        return course;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Course> getMyCourses(String userId) {
        return courseRepository.findAllByInstructorId(UUID.fromString(userId));
    }

    @Override
    @Transactional
    public void deleteCourse(String courseId, String userId, String role, String accountStatus) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            if (!"ROLE_TUTOR".equalsIgnoreCase(role) || !"ACTIVE".equalsIgnoreCase(accountStatus)) {
                throw new AccessDeniedException("Access denied.");
            }
            if (!course.getInstructorId().toString().equals(userId)) {
                throw new AccessDeniedException("Access denied.");
            }
        }
        
        courseRepository.delete(course);
    }
}
