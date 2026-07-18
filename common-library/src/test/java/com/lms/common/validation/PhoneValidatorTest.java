package com.lms.common.validation;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PhoneValidatorTest {

    private final PhoneValidator validator = new PhoneValidator();

    @Test
    void shouldAllowNullOrEmpty() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
    }

    @Test
    void shouldValidateCorrectPhoneNumbers() {
        assertThat(validator.isValid("+1234567890", null)).isTrue();
        assertThat(validator.isValid("1234567890", null)).isTrue();
        assertThat(validator.isValid("123456789012345", null)).isTrue();
    }

    @Test
    void shouldRejectInvalidPhoneNumbers() {
        assertThat(validator.isValid("123", null)).isFalse(); // too short
        assertThat(validator.isValid("1234567890123456", null)).isFalse(); // too long
        assertThat(validator.isValid("phone123", null)).isFalse(); // letters
    }
}
