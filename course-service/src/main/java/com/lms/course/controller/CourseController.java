package com.lms.course.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.course.dto.request.CourseRequest;
import com.lms.course.dto.response.CourseResponse;
import com.lms.course.dto.response.ValidationReportResponse;
import com.lms.course.entity.Course;
import com.lms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Endpoints for managing courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @Operation(summary = "Create course")
    public ResponseEntity<ApiResponse<Void>> createCourse(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody CourseRequest request) {
        courseService.createCourse(userId, role, accountStatus, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course created successfully.")
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course")
    public ResponseEntity<ApiResponse<Void>> updateCourse(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody CourseRequest request) {
        courseService.updateCourse(id, userId, role, accountStatus, request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course updated successfully.")
                        .build()
        );
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish course")
    public ResponseEntity<ApiResponse<Void>> publishCourse(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus) {
        courseService.publishCourse(id, userId, role, accountStatus);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course published successfully.")
                        .build()
        );
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate course for publishing")
    public ResponseEntity<ApiResponse<ValidationReportResponse>> validateCourse(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role) {
        ValidationReportResponse report = courseService.validateCourse(id, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<ValidationReportResponse>builder()
                        .success(true)
                        .message("Course validation report generated.")
                        .data(report)
                        .build()
        );
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive course")
    public ResponseEntity<ApiResponse<Void>> archiveCourse(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus) {
        courseService.archiveCourse(id, userId, role, accountStatus);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course archived successfully.")
                        .build()
        );
    }

    @GetMapping("/published")
    @Operation(summary = "Public list of published courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getPublishedCourses() {
        List<CourseResponse> courses = courseService.getPublishedCourses().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<CourseResponse>>builder()
                        .success(true)
                        .message("Courses fetched successfully.")
                        .data(courses)
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course details")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseDetails(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        Course course = courseService.getCourseDetails(id, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<CourseResponse>builder()
                        .success(true)
                        .message("Course details fetched successfully.")
                        .data(mapToResponse(course))
                        .build()
        );
    }

    @GetMapping("/my")
    @Operation(summary = "Tutor's own courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getMyCourses(
            @RequestHeader("X-User-Id") String userId) {
        List<CourseResponse> courses = courseService.getMyCourses(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<CourseResponse>>builder()
                        .success(true)
                        .message("Courses fetched successfully.")
                        .data(courses)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course by ID (Admin or Instructor)")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus) {
        courseService.deleteCourse(id, userId, role, accountStatus);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Course deleted successfully.")
                        .build()
        );
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

