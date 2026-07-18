package com.lms.content.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.content.dto.request.ResourceRequest;
import com.lms.content.dto.response.ResourceResponse;
import com.lms.content.entity.Resource;
import com.lms.content.service.ResourceService;
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
@RequestMapping("/api/v1/content/resources")
@RequiredArgsConstructor
@Tag(name = "Resources", description = "Endpoints for managing lesson resources")
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping
    @Operation(summary = "Create Resource")
    public ResponseEntity<ApiResponse<Void>> createResource(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody ResourceRequest request) {
        resourceService.createResource(userId, role, accountStatus, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Content created successfully.")
                        .build()
        );
    }

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get Resources for Lesson")
    public ResponseEntity<ApiResponse<List<ResourceResponse>>> getLessonResources(
            @PathVariable String lessonId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role) {
        List<ResourceResponse> resources = resourceService.getLessonResources(lessonId, userId, role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(
                ApiResponse.<List<ResourceResponse>>builder()
                        .success(true)
                        .message("Content fetched successfully.")
                        .data(resources)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Resource")
    public ResponseEntity<ApiResponse<Void>> updateResource(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus,
            @Valid @RequestBody ResourceRequest request) {
        resourceService.updateResource(id, userId, role, accountStatus, request);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Resource updated successfully.")
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Resource")
    public ResponseEntity<ApiResponse<Void>> deleteResource(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-Account-Status") String accountStatus) {
        resourceService.deleteResource(id, userId, role, accountStatus);
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Resource deleted successfully.")
                        .build()
        );
    }

    private ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .lessonId(resource.getLesson().getId())
                .title(resource.getTitle())
                .resourceType(resource.getResourceType())
                .fileUrl(resource.getFileUrl())
                .createdAt(resource.getCreatedAt())
                .build();
    }
}
