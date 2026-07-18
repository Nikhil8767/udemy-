package com.lms.auth.security;

import com.lms.auth.entity.UserCredential;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private UUID id;
    private String email;
    private String password;
    private String accountStatus;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private boolean locked;

    public static UserDetailsImpl build(UserCredential user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        boolean isLocked = user.getLockedUntil() != null && user.getLockedUntil().isAfter(java.time.LocalDateTime.now());
        
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getAccountStatus().name(),
                authorities,
                user.isEnabled(),
                isLocked
        );
    }

    @Override
    public String getUsername() { return email; }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return !locked; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return enabled; }
}
