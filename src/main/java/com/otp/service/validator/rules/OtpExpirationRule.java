package com.otp.service.validator.rules;

import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

@Component
public class OtpExpirationRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(OtpExpirationRule.class);
    private static final int OTP_VALIDITY_MINUTES = 5;

    @Override
    public boolean validate(ValidationContext context) {
        if (context.timestamp() == null) {
            logger.warn("Timestamp is null");
            return false;
        }
        
        if (Instant.now().isAfter(context.timestamp().plusSeconds(OTP_VALIDITY_MINUTES * 60))) {
            logger.warn("OTP has expired");
            return false;
        }
        return true;
    }
} 