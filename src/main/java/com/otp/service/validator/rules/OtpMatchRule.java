package com.otp.service.validator.rules;

import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.otp.service.EncryptionService;

@Component
public class OtpMatchRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(OtpMatchRule.class);
    private final EncryptionService encryptionService;

    public OtpMatchRule(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Override
    public boolean validate(ValidationContext context) {
        if (context.storedOtp() == null || context.providedOtp() == null) {
            logger.warn("OTP is null");
            return false;
        }
        
        try {
            // Use encrypt for OTP validation to ensure consistent encryption
            String encryptedProvidedOtp = encryptionService.encrypt(context.providedOtp());
            boolean isValid = constantTimeEquals(encryptedProvidedOtp, context.storedOtp());
            
            if (!isValid) {
                logger.warn("OTP does not match");
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Error during OTP validation: {}", e.getMessage());
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
} 