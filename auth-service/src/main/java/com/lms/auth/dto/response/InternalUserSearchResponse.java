package com.lms.auth.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class InternalUserSearchResponse {
    private List<UUID> userIds;
    private long totalElements;
    private int totalPages;
}
