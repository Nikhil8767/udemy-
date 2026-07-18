package com.lms.user.dto.response;

import com.lms.common.dto.response.ApiResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidationErrorResponse extends ApiResponse<Void> {
    private List<FieldErrorDto> errors;

    @Getter
    @Setter
    public static class FieldErrorDto {
        private String field;
        private String message;
        
        public FieldErrorDto(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }
}
