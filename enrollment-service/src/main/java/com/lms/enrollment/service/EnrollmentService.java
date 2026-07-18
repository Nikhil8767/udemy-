package com.lms.enrollment.service;

import com.lms.enrollment.dto.request.EnrollmentRequest;
import com.lms.enrollment.dto.request.LessonProgressRequest;
import com.lms.enrollment.entity.Enrollment;

import java.util.List;

public interface EnrollmentService {
    void enroll(String userId, String role, EnrollmentRequest request);
    List<Enrollment> getMyEnrollments(String userId, String role);
    Enrollment getEnrollmentDetails(String courseId, String userId, String role);
    Long getCourseStudentCount(String courseId);
    boolean markLessonCompleted(String courseId, String lessonId, String userId, String role, LessonProgressRequest request);
    Integer getProgress(String courseId, String userId, String role);
    void dropCourse(String courseId, String userId, String role);
}
