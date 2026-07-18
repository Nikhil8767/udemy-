package com.lms.user.service.impl;

import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.user.dto.request.ProfileRequest;
import com.lms.user.entity.UserProfile;
import com.lms.user.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserProfileRepository repository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createProfile_ShouldSaveProfile_WhenDataIsValid() {
        ProfileRequest request = new ProfileRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPhoneNumber("1234567890");

        when(repository.existsByAuthUserId(any())).thenReturn(false);
        when(repository.existsByPhoneNumber("1234567890")).thenReturn(false);

        userService.createProfile(UUID.randomUUID().toString(), request);

        verify(repository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void createProfile_ShouldThrowException_WhenProfileExists() {
        ProfileRequest request = new ProfileRequest();
        when(repository.existsByAuthUserId(any())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createProfile(UUID.randomUUID().toString(), request));
    }

    @Test
    void createProfile_ShouldThrowException_WhenPhoneNumberExists() {
        ProfileRequest request = new ProfileRequest();
        request.setPhoneNumber("1234567890");
        when(repository.existsByAuthUserId(any())).thenReturn(false);
        when(repository.existsByPhoneNumber("1234567890")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.createProfile(UUID.randomUUID().toString(), request));
    }

    @Test
    void getProfile_ShouldReturnProfile_WhenExists() {
        UUID authId = UUID.randomUUID();
        UserProfile profile = UserProfile.builder().firstName("John").build();
        when(repository.findByAuthUserId(authId)).thenReturn(Optional.of(profile));

        UserProfile result = userService.getProfile(authId.toString());

        assertThat(result.getFirstName()).isEqualTo("John");
    }

    @Test
    void getProfile_ShouldThrowException_WhenNotExists() {
        UUID authId = UUID.randomUUID();
        when(repository.findByAuthUserId(authId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(authId.toString()));
    }

    @Test
    void updateProfile_ShouldUpdateFields_WhenValidData() {
        UUID authId = UUID.randomUUID();
        UserProfile existing = UserProfile.builder().authUserId(authId).firstName("Old").build();

        ProfileRequest request = new ProfileRequest();
        request.setFirstName("New");
        request.setPhoneNumber("0987654321");

        when(repository.findByAuthUserId(authId)).thenReturn(Optional.of(existing));
        when(repository.existsByPhoneNumberAndAuthUserIdNot("0987654321", authId)).thenReturn(false);

        userService.updateProfile(authId.toString(), request);

        assertThat(existing.getFirstName()).isEqualTo("New");
        assertThat(existing.getPhoneNumber()).isEqualTo("0987654321");
        verify(repository, times(1)).save(existing);
    }

    @Test
    void updateProfile_ShouldThrowException_WhenPhoneNumberTakenByAnotherUser() {
        UUID authId = UUID.randomUUID();
        UserProfile existing = UserProfile.builder().authUserId(authId).firstName("Old").build();

        ProfileRequest request = new ProfileRequest();
        request.setPhoneNumber("0987654321");

        when(repository.findByAuthUserId(authId)).thenReturn(Optional.of(existing));
        when(repository.existsByPhoneNumberAndAuthUserIdNot("0987654321", authId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.updateProfile(authId.toString(), request));
    }

    @Test
    void getAllProfiles_ShouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(new UserProfile(), new UserProfile()));

        List<UserProfile> result = userService.getAllProfiles();

        assertThat(result).hasSize(2);
    }

    @Test
    void deleteProfile_ShouldDelete_WhenProfileExists() {
        UUID id = UUID.randomUUID();
        UserProfile profile = new UserProfile();
        when(repository.findById(id)).thenReturn(Optional.of(profile));

        userService.deleteProfile(id.toString());

        verify(repository, times(1)).delete(profile);
    }
}
