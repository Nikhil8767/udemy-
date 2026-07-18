package com.lms.gateway.security;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String email;
    private final String token;
    private final String userId;
    private final String accountStatus;
    private final String role;

    public JwtAuthenticationToken(String email, String token, Collection<? extends GrantedAuthority> authorities, String userId, String accountStatus, String role) {
        super(authorities);
        this.email = email;
        this.token = token;
        this.userId = userId;
        this.accountStatus = accountStatus;
        this.role = role;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return email;
    }

    @Override
    public String getName() {
        return email;
    }
}
