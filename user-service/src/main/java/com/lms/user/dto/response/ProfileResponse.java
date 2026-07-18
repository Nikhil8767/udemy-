package com.lms.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProfileResponse {
    private UUID id;
    private UUID authUserId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String bio;
    private String profileImageUrl;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String address;
    private String linkedinUrl;
    private String githubUrl;
    private String websiteUrl;

    // Professional Tutor Fields
    private String displayName;
    private String about;
    private String qualifications;
    private String teachingExperience;
    private String skills;
    private String preferredLanguage;
    
    // Completion metrics
    private Integer completionPercentage;
}
