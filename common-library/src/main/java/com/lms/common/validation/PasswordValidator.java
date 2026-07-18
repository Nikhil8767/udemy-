package com.lms.common.validation;

import com.lms.common.constant.AppConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true; // Let @NotBlank handle nulls
        return value.matches(AppConstants.PASSWORD_REGEX);
    }
}
