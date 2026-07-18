package com.lms.user.service;

import com.lms.user.dto.request.ProfileRequest;
import com.lms.user.entity.UserProfile;

import java.util.List;

public interface UserService {
    void createProfile(String userId, ProfileRequest request);
    UserProfile getProfile(String userId);
    void updateProfile(String userId, ProfileRequest request);
    UserProfile getProfileById(String id);
    List<UserProfile> getAllProfiles();
    void deleteProfile(String id);
}
