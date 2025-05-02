package com.otp.service.validator.rules;

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
    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 15;
    
    private final OtpAttemptRepository attemptRepository;
    
    public AttemptLimitRule(OtpAttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    @Override
    public boolean validate(ValidationContext context) {
        if (context.email() == null) {
            return false;
        }

        String email = context.email().toLowerCase();
        Instant windowStart = Instant.now().minusSeconds(WINDOW_MINUTES * 60);
        
        // Save current attempt
        attemptRepository.save(new OtpAttempt(email, Instant.now()));
        
        // Check number of attempts in window
        int recentAttempts = attemptRepository.findRecentAttempts(email, windowStart).size();
        
        if (recentAttempts > MAX_ATTEMPTS) {
            logger.warn("Too many attempts for email: {}", email);
            return false;
        }
        
        return true;
    }
} 