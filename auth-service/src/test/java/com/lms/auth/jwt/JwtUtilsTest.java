package com.lms.auth.jwt;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // A base64 encoded strong random key for testing
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "4qhq8L6M4qhq8L6M4qhq8L6M4qhq8L6M4qhq8L6M4qhq8L6M4qhq8L6M4qhq8L6M=");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000);
    }

    @Test
    void shouldGenerateAndValidateToken() {
        String token = jwtUtils.generateJwtToken(UUID.randomUUID().toString(), "test@test.com", "ROLE_STUDENT", "ACTIVE");
        
        assertThat(token).isNotBlank();
        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtUtils.generateJwtToken(UUID.randomUUID().toString(), "test@test.com", "ROLE_STUDENT", "ACTIVE");
        
        String extractedEmail = jwtUtils.getUserEmailFromJwtToken(token);
        assertThat(extractedEmail).isEqualTo("test@test.com");
    }

    @Test
    void shouldFailValidationOnInvalidToken() {
        assertThat(jwtUtils.validateJwtToken("invalid-token-string")).isFalse();
    }
}
