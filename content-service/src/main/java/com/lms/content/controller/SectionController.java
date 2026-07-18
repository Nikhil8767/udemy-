package com.lms.content.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.content.dto.request.SectionRequest;
import com.lms.content.dto.response.SectionResponse;
import com.lms.content.entity.Section;
import com.lms.content.service.SectionService;
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
@RequestMapping("/api/v1/content/sections")
@RequiredArgsConstructor
@Tag(name = "Sections", description = "Endpoints for managing course sections")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    @Operation(summary = "Create Section")
    public ResponseEntity<ApiResponse<Void>> createSection(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody SectionRequest request) {
        sectionService.createSection(userId, role, accountStatus, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section created successfully.")
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Section")
    public ResponseEntity<ApiResponse<Void>> updateSection(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody SectionRequest request) {
        sectionService.updateSection(id, userId, role, accountStatus, request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section updated successfully.")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Section")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus) {
        sectionService.deleteSection(id, userId, role, accountStatus);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Section deleted successfully.")
                        .build()
        );
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get Sections for Course")
    public ResponseEntity<ApiResponse<List<SectionResponse>>> getCourseSections(
            @PathVariable String courseId) {
        List<SectionResponse> sections = sectionService.getCourseSections(courseId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<SectionResponse>>builder()
                        .success(true)
                        .message("Content fetched successfully.")
                        .data(sections)
                        .build()
        );
    }

    private SectionResponse mapToResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .courseId(section.getCourseId())
                .title(section.getTitle())
                .description(section.getDescription())
                .displayOrder(section.getDisplayOrder())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}
