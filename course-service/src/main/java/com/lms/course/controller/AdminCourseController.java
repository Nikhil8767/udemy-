package com.lms.course.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.enums.CourseStatus;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.common.exception.UnauthorizedException;
import com.lms.course.entity.Course;
import com.lms.course.repository.CourseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/courses")
@RequiredArgsConstructor
@Tag(name = "Admin Courses", description = "Admin endpoints for course management")
public class AdminCourseController {

    private final CourseRepository courseRepository;

    private void verifyAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    @Data
    public static class AdminCourseSummaryResponse {
        private UUID id;
        private String title;
        private CourseStatus courseStatus;
        private boolean isFeatured;
        private UUID instructorId;
    }

    @Data
    public static class AdminCoursePaginatedResponse {
        private List<AdminCourseSummaryResponse> content;
        private long totalElements;
        private int totalPages;
    }

    @GetMapping
    @Operation(summary = "List all courses")
    public ResponseEntity<ApiResponse<AdminCoursePaginatedResponse>> listCourses(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        verifyAdmin(roleHeader);

        Page<Course> result = courseRepository.searchCourses(
                status, categoryId, instructorId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        
        List<AdminCourseSummaryResponse> content = result.getContent().stream().map(course -> {
            AdminCourseSummaryResponse dto = new AdminCourseSummaryResponse();
            dto.setId(course.getId());
            dto.setTitle(course.getTitle());
            dto.setCourseStatus(course.getCourseStatus());
            dto.setFeatured(course.isFeatured());
            dto.setInstructorId(course.getInstructorId());
            return dto;
        }).collect(Collectors.toList());

        AdminCoursePaginatedResponse responseData = new AdminCoursePaginatedResponse();
        responseData.setContent(content);
        responseData.setTotalElements(result.getTotalElements());
        responseData.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(ApiResponse.<AdminCoursePaginatedResponse>builder()
                .success(true)
                .message("Courses fetched successfully.")
                .data(responseData)
                .build());
    }

    @PatchMapping("/{id}/publish")
    @Operation(summary = "Publish Course")
    public ResponseEntity<ApiResponse<Void>> publishCourse(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setCourseStatus(CourseStatus.PUBLISHED);
        courseRepository.save(course);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Course published.").build());
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive Course")
    public ResponseEntity<ApiResponse<Void>> archiveCourse(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setCourseStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Course archived.").build());
    }

    @PatchMapping("/{id}/feature")
    @Operation(summary = "Feature Course")
    public ResponseEntity<ApiResponse<Void>> featureCourse(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        course.setFeatured(!course.isFeatured());
        courseRepository.save(course);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Course featured flag toggled.").build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Hard Delete Course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        Course course = courseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        courseRepository.delete(course);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Course deleted.").build());
    }
}
