package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.List;

@Data
public class AdminEnrollmentPaginatedResponse {
    private List<AdminEnrollmentResponse> content;
    private long totalElements;
    private int totalPages;
}
