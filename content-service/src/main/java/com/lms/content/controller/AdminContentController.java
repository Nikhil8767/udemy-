package com.lms.content.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.common.exception.UnauthorizedException;
import com.lms.content.entity.Lesson;
import com.lms.content.entity.Resource;
import com.lms.content.entity.Section;
import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.ResourceRepository;
import com.lms.content.repository.SectionRepository;
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
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Content", description = "Admin endpoints for content management")
public class AdminContentController {

    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final ResourceRepository resourceRepository;

    private void verifyAdmin(String role) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(role)) {
            throw new UnauthorizedException("Access denied.");
        }
    }

    @Data
    public static class AdminSectionResponse {
        private UUID id;
        private UUID courseId;
        private String title;
    }

    @Data
    public static class AdminLessonResponse {
        private UUID id;
        private String title;
        private String contentType;
        private Integer displayOrder;
    }

    @Data
    public static class AdminPaginatedResponse<T> {
        private List<T> content;
        private long totalElements;
        private int totalPages;
    }

    @GetMapping("/sections")
    @Operation(summary = "List all sections")
    public ResponseEntity<ApiResponse<AdminPaginatedResponse<AdminSectionResponse>>> listSections(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        verifyAdmin(roleHeader);

        Page<Section> result = sectionRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        List<AdminSectionResponse> content = result.getContent().stream().map(section -> {
            AdminSectionResponse dto = new AdminSectionResponse();
            dto.setId(section.getId());
            dto.setCourseId(section.getCourseId());
            dto.setTitle(section.getTitle());
            return dto;
        }).collect(Collectors.toList());

        AdminPaginatedResponse<AdminSectionResponse> responseData = new AdminPaginatedResponse<>();
        responseData.setContent(content);
        responseData.setTotalElements(result.getTotalElements());
        responseData.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(ApiResponse.<AdminPaginatedResponse<AdminSectionResponse>>builder()
                .success(true)
                .message("Sections fetched successfully.")
                .data(responseData)
                .build());
    }

    @GetMapping("/lessons")
    @Operation(summary = "List all lessons")
    public ResponseEntity<ApiResponse<AdminPaginatedResponse<AdminLessonResponse>>> listLessons(
            @RequestHeader("X-User-Role") String roleHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        verifyAdmin(roleHeader);

        Page<Lesson> result = lessonRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        
        List<AdminLessonResponse> content = result.getContent().stream().map(lesson -> {
            AdminLessonResponse dto = new AdminLessonResponse();
            dto.setId(lesson.getId());
            dto.setTitle(lesson.getTitle());
            dto.setContentType(lesson.getContentType().name());
            dto.setDisplayOrder(lesson.getDisplayOrder());
            return dto;
        }).collect(Collectors.toList());

        AdminPaginatedResponse<AdminLessonResponse> responseData = new AdminPaginatedResponse<>();
        responseData.setContent(content);
        responseData.setTotalElements(result.getTotalElements());
        responseData.setTotalPages(result.getTotalPages());

        return ResponseEntity.ok(ApiResponse.<AdminPaginatedResponse<AdminLessonResponse>>builder()
                .success(true)
                .message("Lessons fetched successfully.")
                .data(responseData)
                .build());
    }

    @DeleteMapping("/lessons/{id}")
    @Operation(summary = "Hard Delete Lesson")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        Lesson lesson = lessonRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        lessonRepository.delete(lesson);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Lesson deleted.").build());
    }

    @DeleteMapping("/resources/{id}")
    @Operation(summary = "Hard Delete Resource")
    public ResponseEntity<ApiResponse<Void>> deleteResource(
            @RequestHeader("X-User-Role") String roleHeader,
            @PathVariable UUID id) {
        verifyAdmin(roleHeader);
        Resource resource = resourceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        resourceRepository.delete(resource);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Resource deleted.").build());
    }
}
