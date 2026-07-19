package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.List;

@Data
public class AdminCoursePaginatedResponse {
    private List<AdminCourseSummaryResponse> content;
    private long totalElements;
    private int totalPages;
}
