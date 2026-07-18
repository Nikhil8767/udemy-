package com.lms.content.service;

import com.lms.content.dto.request.LessonRequest;
import com.lms.content.entity.Lesson;
import java.util.List;

public interface LessonService {
    void createLesson(String userId, String role, String accountStatus, LessonRequest request);
    void updateLesson(String lessonId, String userId, String role, String accountStatus, LessonRequest request);
    void deleteLesson(String lessonId, String userId, String role, String accountStatus);
    Lesson getLesson(String lessonId, String userId, String role);
    List<Lesson> getSectionLessons(String sectionId, String userId, String role);
}
