package com.lms.course.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class ProfileResponse {
    private UUID id;
    private UUID authUserId;
    private String firstName;
    private String lastName;
    private String displayName;
    private Integer completionPercentage;
}
