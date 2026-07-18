package com.lms.user.dto.response;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class InternalUserSearchResponse {
    private List<UUID> userIds;
    private long totalElements;
    private int totalPages;
}
