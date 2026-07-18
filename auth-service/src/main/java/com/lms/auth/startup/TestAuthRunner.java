package com.lms.auth.startup;

import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RegisterRequest;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.auth.service.AuthService;
import com.lms.common.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestAuthRunner implements CommandLineRunner {

    private final AuthService authService;
    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String testEmail = "teststudent@lms.com";
        String testPassword = "Password@123";

        log.info("========================================");
        log.info("TestAuthRunner: Starting authentication test");
        log.info("========================================");

        // Step 1: Register
        try {
            RegisterRequest reg = new RegisterRequest();
            reg.setEmail(testEmail);
            reg.setPassword(testPassword);
            reg.setRole(Role.ROLE_STUDENT);
            authService.register(reg);
            log.info("TestAuthRunner STEP 1: Registration SUCCEEDED");
        } catch (Exception e) {
            log.info("TestAuthRunner STEP 1: Registration skipped ({})", e.getMessage());
        }

        // Step 2: Verify DB state
        Optional<UserCredential> optUser = repository.findByEmail(testEmail);
        if (optUser.isPresent()) {
            UserCredential user = optUser.get();
            log.info("TestAuthRunner STEP 2: DB State:");
            log.info("  ID:              {}", user.getId());
            log.info("  Email:           {}", user.getEmail());
            log.info("  Password Hash:   {}", user.getPassword());
            log.info("  Role:            {}", user.getRole());
            log.info("  AccountStatus:   {}", user.getAccountStatus());
            log.info("  Enabled:         {}", user.isEnabled());
            log.info("  FailedAttempts:  {}", user.getFailedLoginAttempts());
            log.info("  LockedUntil:     {}", user.getLockedUntil());
            log.info("  PasswordMatch:   {}", passwordEncoder.matches(testPassword, user.getPassword()));

            // Step 2b: Reset lock if locked (for testing)
            if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null) {
                log.info("TestAuthRunner STEP 2b: Resetting failed attempts and lock");
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                repository.save(user);
            }
        } else {
            log.error("TestAuthRunner STEP 2: User NOT FOUND in database!");
            return;
        }

        // Step 3: Login
        log.info("TestAuthRunner STEP 3: Attempting login...");
        try {
            LoginRequest login = new LoginRequest();
            login.setEmail(testEmail);
            login.setPassword(testPassword);
            var jwt = authService.login(login);
            log.info("========================================");
            log.info("TestAuthRunner RESULT: LOGIN SUCCEEDED!");
            log.info("  Access Token:    {}...", jwt.getAccessToken().substring(0, Math.min(50, jwt.getAccessToken().length())));
            log.info("  Role:            {}", jwt.getRole());
            log.info("  AccountStatus:   {}", jwt.getAccountStatus());
            log.info("  UserId:          {}", jwt.getUserId());
            log.info("========================================");
        } catch (Exception e) {
            log.error("========================================");
            log.error("TestAuthRunner RESULT: LOGIN FAILED!");
            log.error("  Exception Class: {}", e.getClass().getName());
            log.error("  Message:         {}", e.getMessage());
            log.error("========================================", e);
        }
    }
}
