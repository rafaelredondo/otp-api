package com.otp.service.validator.rules;

import com.otp.config.OtpConfig;
import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

@Component
public class OtpExpirationRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(OtpExpirationRule.class);
    
    private final OtpConfig otpConfig;
    
    public OtpExpirationRule(OtpConfig otpConfig) {
        this.otpConfig = otpConfig;
    }

    @Override
    public boolean validate(ValidationContext context) {
        if (context.timestamp() == null) {
            logger.warn("Timestamp is null");
            return false;
        }
        
        int expirationMinutes = otpConfig.getValidation().getExpirationMinutes();
        
        if (Instant.now().isAfter(context.timestamp().plusSeconds(expirationMinutes * 60))) {
            logger.warn("OTP has expired after {} minutes", expirationMinutes);
            return false;
        }
        return true;
    }
} 