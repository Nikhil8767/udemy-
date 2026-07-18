package com.lms.user.service;

import com.lms.user.entity.UserProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileCompletionService {

    public Integer calculateCompletionPercentage(UserProfile profile, String role) {
        if (profile == null) {
            return 0;
        }

        List<Boolean> fields = new ArrayList<>();

        fields.add(isFilled(profile.getFirstName()));
        fields.add(isFilled(profile.getLastName()));
        fields.add(isFilled(profile.getPhoneNumber()));
        fields.add(isFilled(profile.getDateOfBirth()));
        fields.add(isFilled(profile.getGender()));
        fields.add(isFilled(profile.getCountry()));
        fields.add(isFilled(profile.getState()));
        fields.add(isFilled(profile.getCity()));
        fields.add(isFilled(profile.getProfileImageUrl()));
        fields.add(isFilled(profile.getBio()) || isFilled(profile.getAbout()));
        fields.add(isFilled(profile.getPreferredLanguage()));
        fields.add(isFilled(profile.getSkills()));
        fields.add(isFilled(profile.getQualifications()));

        if ("ROLE_TUTOR".equalsIgnoreCase(role)) {
            fields.add(isFilled(profile.getDisplayName()));
            fields.add(isFilled(profile.getTeachingExperience()));
        }

        long filledCount = fields.stream().filter(Boolean::booleanValue).count();
        return (int) Math.round((double) filledCount / fields.size() * 100);
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
