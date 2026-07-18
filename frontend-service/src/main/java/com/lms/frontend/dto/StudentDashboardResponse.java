package com.lms.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDashboardResponse {
    private int activeCourses;
    private int completedCourses;
    private int totalEnrollments;
    private List<EnrollmentResponse> recentEnrollments;
}
