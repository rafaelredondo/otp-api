package com.otp.service;

import com.otp.config.OtpConfig;
import com.otp.exception.OtpValidationException;
import com.otp.exception.ResourceNotFoundException;
import com.otp.exception.TooManyAttemptsException;
import com.otp.model.OtpHistory;
import com.otp.model.OtpStatus;
import com.otp.repository.OtpHistoryRepository;
import com.otp.service.validator.ValidationContext;
import com.otp.service.validator.OtpValidationRule;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for OTP validation service
 */
public interface OtpValidatorService {
    void storeOtp(@Email String email, OtpHistory newHistory);
    boolean validateOtp(@Email String email, String providedOtp);
    boolean revokeOtp(@Email String email, String reason);
}

/**
 * Default implementation of OtpValidatorService
 */
@Service
class DefaultOtpValidatorService implements OtpValidatorService {
    private final List<OtpValidationRule> validationRules;
    private final OtpHistoryRepository historyRepository;
    private final OtpConfig otpConfig;

    public DefaultOtpValidatorService(List<OtpValidationRule> validationRules, 
                               OtpHistoryRepository historyRepository,
                               OtpConfig otpConfig) {
        this.validationRules = validationRules;
        this.historyRepository = historyRepository;
        this.otpConfig = otpConfig;
    }

    @Override
    @Transactional
    public void storeOtp(@Email String email, OtpHistory newHistory) {
        if (email == null) {
            throw new OtpValidationException("Email cannot be null");
        }
        
        String normalizedEmail = email.toLowerCase();
        
        // Invalidate any active OTP for this email
        historyRepository.findByEmailAndStatus(normalizedEmail, OtpStatus.ACTIVE)
            .ifPresent(history -> {
                history.markAsExpired();
                historyRepository.save(history); // Save the expired OTP
                historyRepository.flush(); // Garante persistência imediata
            });
        
        // Save new history
        historyRepository.save(newHistory);
    }

    @Override
    @Transactional
    public boolean validateOtp(@Email String email, String providedOtp) {
        if (email == null) {
            throw new OtpValidationException("Email cannot be null");
        }
        
        String normalizedEmail = email.toLowerCase();
        Optional<OtpHistory> historyOpt = historyRepository.findByEmailAndStatus(normalizedEmail, OtpStatus.ACTIVE);
        
        if (historyOpt.isEmpty()) {
            throw new ResourceNotFoundException("No active OTP found for email: " + normalizedEmail);
        }

        OtpHistory history = historyOpt.get();
        history.incrementAttempts();
        
        // Check if too many attempts
        if (history.getAttemptCount() > otpConfig.getValidation().getMaxAttempts()) {
            throw new TooManyAttemptsException("Too many invalid attempts for email: " + normalizedEmail);
        }
        
        ValidationContext context = new ValidationContext(
            normalizedEmail,
            providedOtp,
            history.getEncryptedOtp(),
            history.getCreatedAt()
        );

        boolean isValid = validationRules.stream()
            .allMatch(rule -> rule.validate(context));

        if (isValid) {
            history.markAsUsed();
        }

        return isValid;
    }

    @Override
    @Transactional
    public boolean revokeOtp(@Email String email, String reason) {
        if (email == null) {
            throw new OtpValidationException("Email cannot be null");
        }
        
        String normalizedEmail = email.toLowerCase();
        Optional<OtpHistory> historyOpt = historyRepository.findByEmailAndStatus(normalizedEmail, OtpStatus.ACTIVE);
        
        if (historyOpt.isEmpty()) {
            return false;
        }

        historyOpt.get().revoke(reason);
        return true;
    }
} 