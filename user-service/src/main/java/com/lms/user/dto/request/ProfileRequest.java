package com.lms.user.dto.request;

import com.lms.common.validation.ValidPhone;
import com.lms.common.validation.ValidUrl;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {
    @Schema(example = "John")
    @NotBlank(message = "First name is required.")
    @Size(max = 255, message = "First name cannot exceed 255 characters.")
    private String firstName;

    @Schema(example = "Doe")
    @NotBlank(message = "Last name is required.")
    @Size(max = 255, message = "Last name cannot exceed 255 characters.")
    private String lastName;

    @Schema(example = "+1234567890")
    @ValidPhone(message = "Invalid phone number.")
    private String phoneNumber;

    @Schema(example = "1990-01-01")
    private String dateOfBirth;

    @Schema(example = "Male")
    private String gender;

    @Schema(example = "Software Engineer with 10 years of experience.")
    @Size(max = 1000, message = "Bio cannot exceed 1000 characters.")
    private String bio;

    @Schema(example = "https://example.com/image.jpg")
    @ValidUrl(message = "Invalid URL.")
    private String profileImageUrl;

    @Schema(example = "USA")
    private String country;

    @Schema(example = "California")
    private String state;

    @Schema(example = "San Francisco")
    private String city;

    @Schema(example = "94105")
    private String zipCode;

    @Schema(example = "123 Main St")
    @Size(max = 500, message = "Address cannot exceed 500 characters.")
    private String address;

    @Schema(example = "https://linkedin.com/in/johndoe")
    @ValidUrl(message = "Invalid URL.")
    private String linkedinUrl;

    @Schema(example = "https://github.com/johndoe")
    @ValidUrl(message = "Invalid URL.")
    private String githubUrl;

    private String websiteUrl;

    // Professional Tutor Fields
    @Schema(example = "John The Tutor")
    @Size(max = 255, message = "Display name cannot exceed 255 characters.")
    private String displayName;

    @Schema(example = "I am a professional tutor...")
    @Size(max = 2000, message = "About cannot exceed 2000 characters.")
    private String about;

    @Schema(example = "MSc Computer Science")
    @Size(max = 1000, message = "Qualifications cannot exceed 1000 characters.")
    private String qualifications;

    @Schema(example = "5 years teaching Java")
    @Size(max = 1000, message = "Teaching experience cannot exceed 1000 characters.")
    private String teachingExperience;

    @Schema(example = "Java, Spring Boot, Microservices")
    @Size(max = 1000, message = "Skills cannot exceed 1000 characters.")
    private String skills;

    @Schema(example = "English")
    @Size(max = 100, message = "Preferred language cannot exceed 100 characters.")
    private String preferredLanguage;
}
