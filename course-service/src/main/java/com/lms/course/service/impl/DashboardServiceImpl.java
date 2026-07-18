package com.lms.course.service.impl;

import com.lms.common.enums.CourseStatus;
import com.lms.course.dto.response.CourseResponse;
import com.lms.course.dto.response.TutorDashboardResponse;
import com.lms.course.entity.Course;
import com.lms.course.repository.CourseRepository;
import com.lms.course.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public TutorDashboardResponse getTutorDashboard(String userId) {
        List<Course> courses = courseRepository.findAllByInstructorId(UUID.fromString(userId));

        int published = 0;
        int draft = 0;
        int archived = 0;
        long totalStudents = 0;
        long totalEnrollments = 0;

        for (Course c : courses) {
            if (c.getCourseStatus() == CourseStatus.PUBLISHED) published++;
            else if (c.getCourseStatus() == CourseStatus.DRAFT) draft++;
            else if (c.getCourseStatus() == CourseStatus.ARCHIVED) archived++;

            totalStudents += (c.getTotalStudents() != null ? c.getTotalStudents() : 0);
            totalEnrollments += (c.getTotalEnrollments() != null ? c.getTotalEnrollments() : 0);
        }

        List<CourseResponse> recent = courses.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return TutorDashboardResponse.builder()
                .totalCourses(courses.size())
                .publishedCourses(published)
                .draftCourses(draft)
                .archivedCourses(archived)
                .totalStudents(totalStudents)
                .totalEnrollments(totalEnrollments)
                .recentCourses(recent)
                .build();
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .subtitle(course.getSubtitle())
                .description(course.getDescription())
                .shortDescription(course.getShortDescription())
                .thumbnailUrl(course.getThumbnailUrl())
                .bannerUrl(course.getBannerUrl())
                .language(course.getLanguage())
                .courseLevel(course.getCourseLevel())
                .courseStatus(course.getCourseStatus())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .price(course.getPrice())
                .discountPrice(course.getDiscountPrice())
                .currency(course.getCurrency())
                .estimatedDurationMinutes(course.getEstimatedDurationMinutes())
                .instructorId(course.getInstructorId())
                .publishedAt(course.getPublishedAt())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .totalStudents(course.getTotalStudents())
                .totalEnrollments(course.getTotalEnrollments())
                .averageRating(course.getAverageRating())
                .courseCompletionCount(course.getCourseCompletionCount())
                .lastEnrollmentDate(course.getLastEnrollmentDate())
                .build();
    }
}
