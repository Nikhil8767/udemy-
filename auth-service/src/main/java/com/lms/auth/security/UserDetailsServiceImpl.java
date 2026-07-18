package com.lms.auth.security;

import com.lms.auth.entity.UserCredential;
import com.lms.auth.repository.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserCredentialRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("=== UserDetailsService: Loading user by email: {} ===", email);
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("=== UserDetailsService: User NOT FOUND with email: {} ===", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
        
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        log.info("=== UserDetailsService: User loaded. ID={}, Enabled={}, NonLocked={}, NonExpired={}, CredNonExpired={}, Authorities={} ===",
                userDetails.getId(), userDetails.isEnabled(), userDetails.isAccountNonLocked(),
                userDetails.isAccountNonExpired(), userDetails.isCredentialsNonExpired(), userDetails.getAuthorities());
        return userDetails;
    }
}
