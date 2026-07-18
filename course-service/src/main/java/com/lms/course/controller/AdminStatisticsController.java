package com.lms.course.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.enums.CourseStatus;
import com.lms.course.dto.response.CourseStatisticsResponse;
import com.lms.course.repository.CourseRepository;
import com.lms.common.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/courses/statistics")
@RequiredArgsConstructor
@Tag(name = "Admin Course Statistics", description = "Admin endpoints for course statistics")
public class AdminStatisticsController {
    
    private final CourseRepository courseRepository;
    
    @GetMapping
    @Operation(summary = "Course Statistics")
    public ResponseEntity<ApiResponse<CourseStatisticsResponse>> getStatistics(
            @RequestHeader("X-User-Role") String roleHeader) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(roleHeader)) {
            throw new UnauthorizedException("Access denied.");
        }
        CourseStatisticsResponse stats = new CourseStatisticsResponse();
        stats.setTotalCourses(courseRepository.count());
        stats.setPublishedCourses(courseRepository.countByCourseStatus(CourseStatus.PUBLISHED));
        stats.setDraftCourses(courseRepository.countByCourseStatus(CourseStatus.DRAFT));
        stats.setArchivedCourses(courseRepository.countByCourseStatus(CourseStatus.ARCHIVED));
        stats.setFeaturedCourses(courseRepository.countByIsFeaturedTrue());
        
        return ResponseEntity.ok(ApiResponse.<CourseStatisticsResponse>builder()
                .success(true)
                .message("Statistics fetched successfully.")
                .data(stats)
                .build());
    }
}
