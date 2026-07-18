package com.lms.frontend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
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
}
