package com.otp.service.validator.rules;

import com.otp.config.OtpConfig;
import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OtpLengthRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(OtpLengthRule.class);
    
    private final OtpConfig otpConfig;
    
    public OtpLengthRule(OtpConfig otpConfig) {
        this.otpConfig = otpConfig;
    }

    @Override
    public boolean validate(ValidationContext context) {
        int otpLength = otpConfig.getGeneration().getLength();
        
        if (context.providedOtp().length() != otpLength) {
            logger.warn("Invalid OTP length: {}. Expected: {}", 
                context.providedOtp().length(), otpLength);
            return false;
        }
        return true;
    }
} 