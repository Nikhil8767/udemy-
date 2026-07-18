package com.lms.auth.service.impl;

import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RegisterRequest;
import com.lms.auth.dto.response.JwtResponse;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.jwt.JwtUtils;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.auth.security.UserDetailsImpl;
import com.lms.auth.service.AuthService;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import com.lms.common.exception.BusinessException;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (request.getRole() == Role.ROLE_ADMIN) {
            throw new BusinessException("Admin registration is not allowed.");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already registered.");
        }

        UserCredential user = UserCredential.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .accountStatus(AccountStatus.ACTIVE)
                .enabled(true)
                .build();

        repository.save(user);
    }

    @Override
    @Transactional
    public JwtResponse login(LoginRequest request) {
        log.info("=== LOGIN STEP 1: Starting login for email: {} ===", request.getEmail());

        UserCredential user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));
        log.info("=== LOGIN STEP 2: User found. ID={}, Email={}, Role={}, Status={}, Enabled={} ===",
                user.getId(), user.getEmail(), user.getRole(), user.getAccountStatus(), user.isEnabled());
        log.info("=== LOGIN STEP 2b: DB Password hash: {} ===", user.getPassword());
        log.info("=== LOGIN STEP 2c: passwordEncoder.matches() = {} ===",
                passwordEncoder.matches(request.getPassword(), user.getPassword()));

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("=== LOGIN BLOCKED: Account locked until {} ===", user.getLockedUntil());
            throw new UnauthorizedException("Account is temporarily locked.");
        }
        if (user.getAccountStatus() == AccountStatus.REJECTED) {
            log.warn("=== LOGIN BLOCKED: Account status is REJECTED ===");
            throw new UnauthorizedException("Account has been rejected.");
        }
        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            log.warn("=== LOGIN BLOCKED: Account status is SUSPENDED ===");
            throw new UnauthorizedException("Account has been suspended.");
        }
        log.info("=== LOGIN STEP 3: All pre-checks passed ===");

        try {
            log.info("=== LOGIN STEP 4: Calling authenticationManager.authenticate() ===");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            log.info("=== LOGIN STEP 5: AuthenticationManager SUCCEEDED. Principal type: {} ===",
                    authentication.getPrincipal().getClass().getName());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            log.info("=== LOGIN STEP 6: UserDetails extracted. ID={}, Email={}, Enabled={}, NonLocked={} ===",
                    userDetails.getId(), userDetails.getEmail(), userDetails.isEnabled(), userDetails.isAccountNonLocked());

            String jwt = jwtUtils.generateJwtToken(
                    userDetails.getId().toString(),
                    userDetails.getEmail(),
                    user.getRole().name(),
                    user.getAccountStatus().name());
            log.info("=== LOGIN STEP 7: JWT generated successfully. Token length: {} ===", jwt.length());

            user.setLastLogin(LocalDateTime.now());
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            repository.save(user);
            log.info("=== LOGIN STEP 8: User record updated (lastLogin, failedAttempts reset) ===");

            JwtResponse jwtResponse = JwtResponse.builder()
                    .accessToken(jwt)
                    .expiresIn(jwtExpirationMs / 1000)
                    .userId(userDetails.getId().toString())
                    .email(userDetails.getEmail())
                    .role(user.getRole().name())
                    .accountStatus(user.getAccountStatus().name())
                    .build();
            log.info("=== LOGIN STEP 9: JwtResponse built. Role={}, AccountStatus={}, ExpiresIn={} ===",
                    jwtResponse.getRole(), jwtResponse.getAccountStatus(), jwtResponse.getExpiresIn());
            log.info("=== LOGIN COMPLETE: Returning successful response ===");
            return jwtResponse;
        } catch (BadCredentialsException e) {
            log.error("=== LOGIN FAILED at STEP 4: BadCredentialsException: {} ===", e.getMessage());
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                repository.save(user);
                throw new UnauthorizedException("Account is temporarily locked.");
            }
            repository.save(user);
            throw new UnauthorizedException("Invalid email or password.");
        } catch (LockedException e) {
            log.error("=== LOGIN FAILED: LockedException for email: {} ===", request.getEmail());
            throw new UnauthorizedException("Account is temporarily locked.");
        } catch (DisabledException e) {
            log.error("=== LOGIN FAILED: DisabledException for email: {} ===", request.getEmail());
            throw new UnauthorizedException("Account disabled.");
        } catch (UnauthorizedException e) {
            log.error("=== LOGIN FAILED: UnauthorizedException (re-throwing): {} ===", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("=== LOGIN FAILED at UNKNOWN STEP: Exception class={}, Message={} ===",
                    e.getClass().getName(), e.getMessage(), e);
            throw new UnauthorizedException("Authentication failed: " + e.getMessage());
        }
    }

    @Override
    public UserCredential getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("Unauthorized access.");
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return repository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }
}
