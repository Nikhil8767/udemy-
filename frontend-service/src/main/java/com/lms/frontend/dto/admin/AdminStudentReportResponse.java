package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.UUID;

@Data
public class AdminStudentReportResponse {
    private UUID studentId;
    private long totalEnrolled;
    private long completed;
    private long dropped;
}
