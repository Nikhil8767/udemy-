package com.lms.content.dto.response;

import lombok.Data;

@Data
public class ContentStatisticsResponse {
    private long totalSections;
    private long totalLessons;
    private long totalResources;
}
