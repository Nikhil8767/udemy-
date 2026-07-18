package com.lms.course.service;

import com.lms.course.dto.request.CourseRequest;
import com.lms.course.entity.Course;

import java.util.List;
import com.lms.course.dto.response.ValidationReportResponse;

public interface CourseService {
    void createCourse(String userId, String role, String accountStatus, CourseRequest request);
    void updateCourse(String courseId, String userId, String role, String accountStatus, CourseRequest request);
    void publishCourse(String courseId, String userId, String role, String accountStatus);
    void archiveCourse(String courseId, String userId, String role, String accountStatus);
    ValidationReportResponse validateCourse(String courseId, String userId, String role);
    List<Course> getPublishedCourses();
    Course getCourseDetails(String courseId, String userId, String role);
    List<Course> getMyCourses(String userId);
    void deleteCourse(String courseId, String userId, String role, String accountStatus);
}
