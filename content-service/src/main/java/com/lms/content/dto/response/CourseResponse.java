package com.lms.content.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class CourseResponse {
    private UUID id;
    private UUID instructorId;
}
