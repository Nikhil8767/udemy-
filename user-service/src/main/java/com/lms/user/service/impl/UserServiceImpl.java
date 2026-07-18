package com.lms.user.service.impl;

import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.user.dto.request.ProfileRequest;
import com.lms.user.entity.UserProfile;
import com.lms.user.repository.UserProfileRepository;
import com.lms.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository repository;

    @Override
    @Transactional
    public void createProfile(String userId, ProfileRequest request) {
        UUID authUserId = UUID.fromString(userId);
        if (repository.existsByAuthUserId(authUserId)) {
            throw new DuplicateResourceException("Profile already exists.");
        }
        if (request.getPhoneNumber() != null && repository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateResourceException("Phone number already exists.");
        }

        UserProfile profile = UserProfile.builder()
                .authUserId(authUserId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .bio(request.getBio())
                .profileImageUrl(request.getProfileImageUrl())
                .country(request.getCountry())
                .state(request.getState())
                .city(request.getCity())
                .zipCode(request.getZipCode())
                .address(request.getAddress())
                .linkedinUrl(request.getLinkedinUrl())
                .githubUrl(request.getGithubUrl())
                .websiteUrl(request.getWebsiteUrl())
                .displayName(request.getDisplayName())
                .about(request.getAbout())
                .qualifications(request.getQualifications())
                .teachingExperience(request.getTeachingExperience())
                .skills(request.getSkills())
                .preferredLanguage(request.getPreferredLanguage())
                .build();
        
        repository.save(profile);
    }

    @Override
    public UserProfile getProfile(String userId) {
        return repository.findByAuthUserId(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found."));
    }

    @Override
    @Transactional
    public void updateProfile(String userId, ProfileRequest request) {
        UserProfile profile;
        try {
            profile = getProfile(userId);
        } catch (ResourceNotFoundException e) {
            // Upsert: Create profile if it does not exist
            createProfile(userId, request);
            return;
        }
        
        if (request.getPhoneNumber() != null && repository.existsByPhoneNumberAndAuthUserIdNot(request.getPhoneNumber(), profile.getAuthUserId())) {
            throw new DuplicateResourceException("Phone number already exists.");
        }

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setGender(request.getGender());
        profile.setBio(request.getBio());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setCountry(request.getCountry());
        profile.setState(request.getState());
        profile.setCity(request.getCity());
        profile.setZipCode(request.getZipCode());
        profile.setAddress(request.getAddress());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGithubUrl(request.getGithubUrl());
        profile.setWebsiteUrl(request.getWebsiteUrl());
        profile.setDisplayName(request.getDisplayName());
        profile.setAbout(request.getAbout());
        profile.setQualifications(request.getQualifications());
        profile.setTeachingExperience(request.getTeachingExperience());
        profile.setSkills(request.getSkills());
        profile.setPreferredLanguage(request.getPreferredLanguage());

        repository.save(profile);
    }

    @Override
    public UserProfile getProfileById(String id) {
        return repository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found."));
    }

    @Override
    public List<UserProfile> getAllProfiles() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteProfile(String id) {
        UserProfile profile = getProfileById(id);
        repository.delete(profile);
    }
}
