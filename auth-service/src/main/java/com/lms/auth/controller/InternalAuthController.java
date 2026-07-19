package com.lms.auth.controller;

import com.lms.auth.dto.response.InternalUserSearchResponse;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Hidden
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalAuthController {

    private final UserCredentialRepository userCredentialRepository;

    @GetMapping("/search")
    public ResponseEntity<InternalUserSearchResponse> searchUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<UserCredential> result = userCredentialRepository.findByRoleAndStatus(
                role, 
                status, 
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        java.util.List<InternalUserSearchResponse.AuthUserDto> users = result.getContent().stream()
                .map(u -> InternalUserSearchResponse.AuthUserDto.builder()
                        .id(u.getId())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .accountStatus(u.getAccountStatus().name())
                        .createdAt(u.getCreatedAt())
                        .lastLoginAt(u.getLastLogin())
                        .build())
                .collect(java.util.stream.Collectors.toList());

        java.util.List<UUID> userIds = result.getContent().stream()
                .map(UserCredential::getId)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(InternalUserSearchResponse.builder()
                .userIds(userIds)
                .users(users)
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id, @RequestParam AccountStatus status) {
        UserCredential credential = userCredentialRepository.findById(id).orElse(null);
        if (credential != null) {
            credential.setAccountStatus(status);
            userCredentialRepository.save(credential);
        }
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> updateRole(@PathVariable UUID id, @RequestParam Role role) {
        UserCredential credential = userCredentialRepository.findById(id).orElse(null);
        if (credential != null) {
            credential.setRole(role);
            userCredentialRepository.save(credential);
        }
        return ResponseEntity.ok().build();
    }
}
