package com.lms.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCourseDTO {
    private CourseResponse course;
    private EnrollmentResponse enrollment;
}
