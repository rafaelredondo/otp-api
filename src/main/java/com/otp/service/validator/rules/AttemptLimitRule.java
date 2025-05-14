package com.otp.service.validator.rules;

import com.otp.config.OtpConfig;
import com.otp.exception.TooManyAttemptsException;
import com.otp.model.OtpAttempt;
import com.otp.repository.OtpAttemptRepository;
import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.ValidationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

@Component
public class AttemptLimitRule implements OtpValidationRule {
    private static final Logger logger = LoggerFactory.getLogger(AttemptLimitRule.class);
    
    private final OtpAttemptRepository attemptRepository;
    private final OtpConfig otpConfig;
    
    public AttemptLimitRule(OtpAttemptRepository attemptRepository, OtpConfig otpConfig) {
        this.attemptRepository = attemptRepository;
        this.otpConfig = otpConfig;
    }

    @Override
    public boolean validate(ValidationContext context) {
        if (context.email() == null) {
            return false;
        }

        String email = context.email().toLowerCase();
        int windowMinutes = otpConfig.getValidation().getWindowMinutes();
        int maxAttempts = otpConfig.getValidation().getMaxAttempts();
        
        Instant windowStart = Instant.now().minusSeconds(windowMinutes * 60);
        
        // Save current attempt
        attemptRepository.save(new OtpAttempt(email, Instant.now()));
        
        // Check number of attempts in window
        int recentAttempts = attemptRepository.findRecentAttempts(email, windowStart).size();
        
        if (recentAttempts >= maxAttempts) {
            logger.warn("Too many attempts for email: {}", email);
            throw new TooManyAttemptsException("Too many validation attempts for email: " + email + 
                    ". Try again after " + windowMinutes + " minutes.");
        }
        
        return true;
    }
} 