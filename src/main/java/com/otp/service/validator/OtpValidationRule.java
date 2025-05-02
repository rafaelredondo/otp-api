package com.otp.service.validator;

public interface OtpValidationRule {
    boolean validate(ValidationContext context);
} 