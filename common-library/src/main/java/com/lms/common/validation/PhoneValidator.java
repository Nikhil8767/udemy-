package com.lms.common.validation;

import com.lms.common.constant.AppConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true; // Let @NotBlank handle nulls if required
        return value.matches(AppConstants.PHONE_REGEX);
    }
}
