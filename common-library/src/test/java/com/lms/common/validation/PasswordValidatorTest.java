package com.lms.common.validation;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PasswordValidatorTest {

    private final PasswordValidator validator = new PasswordValidator();

    @Test
    void shouldAllowNullOrEmpty() {
        // Validation relies on @NotBlank for null/empty checks
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void shouldValidateCorrectPassword() {
        assertThat(validator.isValid("Password123@", null)).isTrue();
        assertThat(validator.isValid("StrongPass@1", null)).isTrue();
    }

    @Test
    void shouldRejectWeakPasswords() {
        assertThat(validator.isValid("password", null)).isFalse(); // no uppercase, no number
        assertThat(validator.isValid("PASSWORD", null)).isFalse(); // no lowercase, no number
        assertThat(validator.isValid("Pass12", null)).isFalse(); // length < 8
        assertThat(validator.isValid("Password123", null)).isFalse(); // no special char
    }
}
