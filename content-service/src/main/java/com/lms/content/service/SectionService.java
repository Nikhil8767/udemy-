package com.lms.content.service;

import com.lms.content.dto.request.SectionRequest;
import com.lms.content.entity.Section;
import java.util.List;

public interface SectionService {
    void createSection(String userId, String role, String accountStatus, SectionRequest request);
    void updateSection(String sectionId, String userId, String role, String accountStatus, SectionRequest request);
    void deleteSection(String sectionId, String userId, String role, String accountStatus);
    List<Section> getCourseSections(String courseId);
}
