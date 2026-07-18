package com.lms.content.controller;

import com.lms.common.dto.response.ApiResponse;
import com.lms.common.exception.UnauthorizedException;
import com.lms.content.dto.response.ContentStatisticsResponse;
import com.lms.content.repository.LessonRepository;
import com.lms.content.repository.ResourceRepository;
import com.lms.content.repository.SectionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/content/statistics")
@RequiredArgsConstructor
@Tag(name = "Admin Content Statistics", description = "Admin endpoints for content statistics")
public class AdminStatisticsController {
    
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final ResourceRepository resourceRepository;
    
    @GetMapping
    @Operation(summary = "Content Statistics")
    public ResponseEntity<ApiResponse<ContentStatisticsResponse>> getStatistics(
            @RequestHeader("X-User-Role") String roleHeader) {
        if (!"ROLE_ADMIN".equalsIgnoreCase(roleHeader)) {
            throw new UnauthorizedException("Access denied.");
        }
        ContentStatisticsResponse stats = new ContentStatisticsResponse();
        stats.setTotalSections(sectionRepository.count());
        stats.setTotalLessons(lessonRepository.count());
        stats.setTotalResources(resourceRepository.count());
        
        return ResponseEntity.ok(ApiResponse.<ContentStatisticsResponse>builder()
                .success(true)
                .message("Statistics fetched successfully.")
                .data(stats)
                .build());
    }
}
