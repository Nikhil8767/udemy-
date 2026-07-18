package com.lms.auth.security;

import com.lms.auth.entity.UserCredential;
import com.lms.auth.repository.UserCredentialRepository;
import com.lms.common.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserCredentialRepository repository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        UserCredential user = new UserCredential();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setPassword("password");
        user.setRole(Role.ROLE_STUDENT);
        user.setAccountStatus(com.lms.common.enums.AccountStatus.ACTIVE);
        user.setEnabled(true);

        when(repository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = service.loadUserByUsername("test@test.com");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("test@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities()).hasSize(1);
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(repository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("notfound@test.com"));
    }
}
