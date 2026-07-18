package com.lms.content.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.content.dto.request.LessonRequest;
import com.lms.content.dto.response.LessonResponse;
import com.lms.content.entity.Lesson;
import com.lms.content.service.LessonService;
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
@RequestMapping("/api/v1/content/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Endpoints for managing course lessons")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    @Operation(summary = "Create Lesson")
    public ResponseEntity<ApiResponse<Void>> createLesson(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody LessonRequest request) {
        lessonService.createLesson(userId, role, accountStatus, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Lesson created successfully.")
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Lesson")
    public ResponseEntity<ApiResponse<Void>> updateLesson(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody LessonRequest request) {
        lessonService.updateLesson(id, userId, role, accountStatus, request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Lesson updated successfully.")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Lesson")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus) {
        lessonService.deleteLesson(id, userId, role, accountStatus);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Lesson deleted successfully.")
                        .build()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Lesson details")
    public ResponseEntity<ApiResponse<LessonResponse>> getLesson(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        Lesson lesson = lessonService.getLesson(id, userId, role);
        return ResponseEntity.ok(
                ApiResponse.<LessonResponse>builder()
                        .success(true)
                        .message("Content fetched successfully.")
                        .data(mapToResponse(lesson))
                        .build()
        );
    }

    @GetMapping("/section/{sectionId}")
    @Operation(summary = "Get Lessons for Section")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getSectionLessons(
            @PathVariable String sectionId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        List<LessonResponse> lessons = lessonService.getSectionLessons(sectionId, userId, role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<LessonResponse>>builder()
                        .success(true)
                        .message("Content fetched successfully.")
                        .data(lessons)
                        .build()
        );
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .sectionId(lesson.getSection().getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .contentType(lesson.getContentType())
                .videoUrl(lesson.getVideoUrl())
                .pdfUrl(lesson.getPdfUrl())
                .articleContent(lesson.getArticleContent())
                .durationMinutes(lesson.getDurationMinutes())
                .isPreview(lesson.isPreview())
                .displayOrder(lesson.getDisplayOrder())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
