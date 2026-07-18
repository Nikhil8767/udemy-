package com.lms.common.dto.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void shouldCreateApiResponseUsingBuilder() {
        LocalDateTime now = LocalDateTime.now();
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Test message")
                .data("Payload")
                .path("/api/test")
                .timestamp(now)
                .build();

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Test message");
        assertThat(response.getData()).isEqualTo("Payload");
        assertThat(response.getPath()).isEqualTo("/api/test");
        assertThat(response.getTimestamp()).isEqualTo(now);
    }

    @Test
    void shouldSetDefaultTimestampIfNotProvided() {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .build();

        assertThat(response.getTimestamp()).isNotNull();
    }
}
