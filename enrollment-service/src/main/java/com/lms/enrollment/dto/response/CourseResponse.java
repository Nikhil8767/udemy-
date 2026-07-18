package com.lms.enrollment.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private String courseStatus;
}
