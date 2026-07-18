package com.lms.common.constant;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class AppConstantsTest {

    @Test
    void testConstants() {
        assertThat(AppConstants.API_V1).isNotBlank();
        assertThat(AppConstants.AUTH_API).isNotBlank();
        assertThat(AppConstants.USERS_API).isNotBlank();
        assertThat(AppConstants.COURSES_API).isNotBlank();
        assertThat(AppConstants.AUTH_HEADER).isNotBlank();
        assertThat(AppConstants.BEARER_PREFIX).isNotBlank();
        assertThat(AppConstants.PASSWORD_REGEX).isNotBlank();
        assertThat(AppConstants.PHONE_REGEX).isNotBlank();
    }
}
