package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminEnrollmentResponse {
    private UUID id;
    private UUID studentId;
    private UUID courseId;
    private String status;
    private Integer progressPercentage;
}
