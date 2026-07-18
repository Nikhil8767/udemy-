package com.lms.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID authUserId;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String phoneNumber;

    private String dateOfBirth;
    private String gender;

    @Column(length = 1000)
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
    
    @Column(length = 2000)
    private String about;
    
    @Column(length = 1000)
    private String qualifications;
    
    @Column(length = 1000)
    private String teachingExperience;
    
    @Column(length = 1000)
    private String skills;
    
    private String preferredLanguage;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
