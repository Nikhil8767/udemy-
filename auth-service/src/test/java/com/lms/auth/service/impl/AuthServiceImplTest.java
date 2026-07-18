package com.lms.auth.service.impl;

import com.lms.auth.dto.request.LoginRequest;
import com.lms.auth.dto.request.RegisterRequest;
import com.lms.auth.dto.response.JwtResponse;
import com.lms.auth.entity.UserCredential;
import com.lms.auth.jwt.JwtUtils;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.auth.security.UserDetailsImpl;
import com.lms.common.enums.AccountStatus;
import com.lms.common.enums.Role;
import com.lms.common.exception.BusinessException;
import com.lms.common.exception.DuplicateResourceException;
import com.lms.common.exception.ResourceNotFoundException;
import com.lms.common.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserCredentialRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtils jwtUtils;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 3600000L);
    }

    @Test
    void register_ShouldSaveUser_WhenValidRequest() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass");
        req.setRole(Role.ROLE_STUDENT);

        when(repository.existsByEmail(req.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        authService.register(req);

        verify(repository, times(1)).save(any(UserCredential.class));
    }

    @Test
    void register_ShouldThrowException_WhenRoleIsAdmin() {
        RegisterRequest req = new RegisterRequest();
        req.setRole(Role.ROLE_ADMIN);

        assertThrows(BusinessException.class, () -> authService.register(req));
        verifyNoInteractions(repository);
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@test.com");
        req.setRole(Role.ROLE_STUDENT);

        when(repository.existsByEmail(req.getEmail())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(req));
    }

    @Test
    void login_ShouldReturnJwt_WhenValidCredentials() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("pass");

        UserCredential user = new UserCredential();
        user.setId(UUID.randomUUID());
        user.setEmail(req.getEmail());
        user.setRole(Role.ROLE_STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(anyString(), anyString(), anyString(), anyString())).thenReturn("token");

        JwtResponse res = authService.login(req);

        assertThat(res.getAccessToken()).isEqualTo("token");
        verify(repository, times(1)).save(any(UserCredential.class)); // Updates last login
    }



    @Test
    void login_ShouldLockAccount_AfterFiveFailedAttempts() {
        LoginRequest req = new LoginRequest();
        req.setEmail("test@test.com");
        req.setPassword("wrong");

        UserCredential user = new UserCredential();
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setFailedLoginAttempts(4); // 5th attempt will lock

        when(repository.findByEmail(req.getEmail())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad"));

        assertThrows(UnauthorizedException.class, () -> authService.login(req));
        
        assertThat(user.getLockedUntil()).isNotNull();
        verify(repository, times(1)).save(user);
    }
    
    @Test
    void getAuthenticatedUser_ShouldReturnUser_WhenAuthenticated() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        UserCredential user = new UserCredential();
        user.setEmail("test@test.com");
        user.setRole(Role.ROLE_STUDENT);
        user.setAccountStatus(AccountStatus.ACTIVE);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        
        UserCredential result = authService.getAuthenticatedUser();
        
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }
}
