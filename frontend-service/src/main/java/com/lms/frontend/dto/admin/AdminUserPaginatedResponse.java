package com.lms.frontend.dto.admin;

import lombok.Data;
import java.util.List;

@Data
public class AdminUserPaginatedResponse {
    private List<AdminUserSummaryResponse> content;
    private long totalElements;
    private int totalPages;
}
