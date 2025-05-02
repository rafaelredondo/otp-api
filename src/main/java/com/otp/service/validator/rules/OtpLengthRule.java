package com.otp.service.validator.rules;

import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OtpLengthRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(OtpLengthRule.class);
    private static final int OTP_LENGTH = 6;

    @Override
    public boolean validate(ValidationContext context) {
        if (context.providedOtp().length() != OTP_LENGTH) {
            logger.warn("Invalid OTP length: {}", context.providedOtp().length());
            return false;
        }
        return true;
    }
} 