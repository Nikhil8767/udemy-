package com.lms.frontend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String bio;
    private String profileImageUrl; // Replaced profilePictureUrl to match backend
    
    // Additional fields mapped from backend
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
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
    
    // Completion Metrics
    private Integer completionPercentage;
}
