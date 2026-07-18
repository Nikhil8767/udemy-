package com.lms.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorDashboardResponse {
    private int totalCourses;
    private int publishedCourses;
    private int draftCourses;
    private int archivedCourses;
    private long totalStudents;
    private long totalEnrollments;
    private List<CourseResponse> recentCourses;
}
