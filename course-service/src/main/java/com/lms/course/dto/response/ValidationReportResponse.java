package com.lms.course.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationReportResponse {
    private boolean profileComplete;
    private boolean courseInfoComplete;
    private boolean thumbnailSet;
    private boolean bannerSet;
    private boolean categorySet;
    private boolean courseLevelSet;
    private boolean hasSections;
    private boolean allSectionsHaveLessons;
    private boolean allLessonsHaveResources;
    private boolean readyToPublish;
    private List<String> errors;
}
