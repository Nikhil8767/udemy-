package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminCourseSummaryResponse {
    private UUID id;
    private String title;
    private String courseStatus;
    private boolean isFeatured;
    private UUID instructorId;
}
