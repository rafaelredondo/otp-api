package com.otp.service.validator.rules;

import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotNullRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(NotNullRule.class);

    @Override
    public boolean validate(ValidationContext context) {
        if (context.email() == null) {
            logger.warn("Email is null");
            return false;
        }
        if (context.providedOtp() == null) {
            logger.warn("OTP is null");
            return false;
        }
        return true;
    }
} 