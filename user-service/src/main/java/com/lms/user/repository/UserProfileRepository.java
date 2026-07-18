package com.lms.user.repository;

import com.lms.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findByAuthUserId(UUID authUserId);
    boolean existsByAuthUserId(UUID authUserId);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumberAndAuthUserIdNot(String phoneNumber, UUID authUserId);
    List<UserProfile> findAllByAuthUserIdIn(List<UUID> authUserIds);
}
