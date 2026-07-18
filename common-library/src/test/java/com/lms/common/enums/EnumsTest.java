package com.lms.common.enums;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EnumsTest {

    @Test
    void testAccountStatus() {
        assertThat(AccountStatus.values()).containsExactly(
                AccountStatus.PENDING,
                AccountStatus.ACTIVE,
                AccountStatus.REJECTED,
                AccountStatus.SUSPENDED
        );
        assertThat(AccountStatus.valueOf("ACTIVE")).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    void testCourseLevel() {
        assertThat(CourseLevel.values()).isNotEmpty();
    }

    @Test
    void testCourseStatus() {
        assertThat(CourseStatus.values()).isNotEmpty();
    }

    @Test
    void testRole() {
        assertThat(Role.values()).containsExactly(
                Role.ROLE_STUDENT,
                Role.ROLE_TUTOR,
                Role.ROLE_ADMIN
        );
    }
}
