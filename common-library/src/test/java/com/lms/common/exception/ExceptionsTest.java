package com.lms.common.exception;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void testBusinessException() {
        BusinessException ex = new BusinessException("Error");
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void testDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Error");
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void testForbiddenException() {
        ForbiddenException ex = new ForbiddenException("Error");
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Error");
        assertThat(ex.getMessage()).isEqualTo("Error");
    }

    @Test
    void testUnauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Error");
        assertThat(ex.getMessage()).isEqualTo("Error");
    }
}
