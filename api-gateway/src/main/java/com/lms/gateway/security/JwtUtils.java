package com.lms.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().verifyWith(key()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            log.error("JWT Validation failed"); // Do not expose stack traces or specific parser errors to users
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public String getUserId(String token) {
        return getClaims(token).get("userId", String.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getAccountStatus(String token) {
        return getClaims(token).get("accountStatus", String.class);
    }
}
