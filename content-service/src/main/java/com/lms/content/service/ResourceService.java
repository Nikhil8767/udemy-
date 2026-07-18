package com.lms.content.service;

import com.lms.content.dto.request.ResourceRequest;
import com.lms.content.entity.Resource;
import java.util.List;

public interface ResourceService {
    void createResource(String userId, String role, String accountStatus, ResourceRequest request);
    List<Resource> getLessonResources(String lessonId, String userId, String role);
    void updateResource(String id, String userId, String role, String accountStatus, ResourceRequest request);
    void deleteResource(String id, String userId, String role, String accountStatus);
}
