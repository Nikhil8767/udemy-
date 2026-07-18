package com.lms.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) return true; // Let @NotBlank handle nulls
        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
