package com.lms.auth.controller;

import com.lms.auth.repository.UserCredentialRepository;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/internal/statistics")
@RequiredArgsConstructor
public class InternalStatisticsController {

    private final UserCredentialRepository userCredentialRepository;

    @Data
    public static class AuthStatistics {
        private long totalUsers;
        private long totalStudents;
        private long totalTutors;
        private long pendingTutorRequests;
    }

    @GetMapping
    public ResponseEntity<AuthStatistics> getAuthStatistics() {
        AuthStatistics stats = new AuthStatistics();
        stats.setTotalUsers(userCredentialRepository.count());
        stats.setTotalStudents(userCredentialRepository.countByRole(Role.ROLE_STUDENT));
        stats.setTotalTutors(userCredentialRepository.countByRole(Role.ROLE_TUTOR));
        stats.setPendingTutorRequests(userCredentialRepository.countByAccountStatus(AccountStatus.PENDING));
        return ResponseEntity.ok(stats);
    }
}
