package com.lms.common.validation;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class UrlValidatorTest {

    private final UrlValidator validator = new UrlValidator();

    @Test
    void shouldAllowNullOrEmpty() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("", null)).isTrue();
    }

    @Test
    void shouldValidateCorrectUrls() {
        assertThat(validator.isValid("http://example.com", null)).isTrue();
        assertThat(validator.isValid("https://example.com/path?query=1", null)).isTrue();
    }

    @Test
    void shouldRejectInvalidUrls() {
        assertThat(validator.isValid("example.com", null)).isFalse();
        assertThat(validator.isValid("htp://example.com", null)).isFalse();
    }
}
