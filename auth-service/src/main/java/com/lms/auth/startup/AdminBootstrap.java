package com.lms.auth.startup;

import com.lms.auth.entity.UserCredential;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements ApplicationRunner {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@lms.com";
        if (!repository.existsByEmail(adminEmail)) {
            UserCredential admin = UserCredential.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ROLE_ADMIN)
                    .accountStatus(AccountStatus.ACTIVE)
                    .enabled(true)
                    .emailVerified(true)
                    .build();
            repository.save(admin);
            log.info("Bootstrap Admin created automatically: {}", adminEmail);
        }
    }
}
