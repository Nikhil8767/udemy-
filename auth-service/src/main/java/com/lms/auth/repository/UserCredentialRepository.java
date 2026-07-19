package com.lms.auth.repository;

import com.lms.auth.entity.UserCredential;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {
    Optional<UserCredential> findByEmail(String email);
    boolean existsByEmail(String email);

    long countByRole(Role role);
    long countByAccountStatus(AccountStatus accountStatus);

    @Query("SELECT u.id FROM UserCredential u WHERE (:role IS NULL OR u.role = :role) AND (:status IS NULL OR u.accountStatus = :status)")
    Page<UUID> findIdsByRoleAndStatus(@Param("role") Role role, @Param("status") AccountStatus status, Pageable pageable);

    @Query("SELECT u FROM UserCredential u WHERE (:role IS NULL OR u.role = :role) AND (:status IS NULL OR u.accountStatus = :status)")
    Page<UserCredential> findByRoleAndStatus(@Param("role") Role role, @Param("status") AccountStatus status, Pageable pageable);
}
