package com.lms.course.dto.response;

import lombok.Data;

@Data
public class CourseStatisticsResponse {
    private long totalCourses;
    private long publishedCourses;
    private long draftCourses;
    private long archivedCourses;
    private long featuredCourses;
}
