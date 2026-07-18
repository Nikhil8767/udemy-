package com.lms.enrollment.client;

import com.lms.common.dto.response.ApiResponse;
import com.lms.enrollment.dto.response.LessonResponse;
import com.lms.enrollment.dto.response.SectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "content-service", path = "/api/v1/content")
public interface ContentServiceClient {
    @GetMapping("/sections/course/{courseId}")
    ApiResponse<List<SectionResponse>> getCourseSections(
            @PathVariable("courseId") String courseId
    );

    @GetMapping("/lessons/section/{sectionId}")
    ApiResponse<List<LessonResponse>> getSectionLessons(
            @PathVariable("sectionId") String sectionId
    );
    
    @GetMapping("/lessons/{id}")
    ApiResponse<LessonResponse> getLesson(
            @PathVariable("id") String id
    );
}
