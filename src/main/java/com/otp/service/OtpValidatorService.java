package com.otp.service;

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

@Service
public class OtpValidatorService {
    private final List<OtpValidationRule> validationRules;
    private final OtpHistoryRepository historyRepository;

    public OtpValidatorService(List<OtpValidationRule> validationRules, OtpHistoryRepository historyRepository) {
        this.validationRules = validationRules;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void storeOtp(@Email String email, OtpHistory newHistory) {
        String normalizedEmail = email.toLowerCase();
        
        // Invalidate any active OTP for this email
        historyRepository.findByEmailAndStatus(normalizedEmail, OtpStatus.ACTIVE)
            .ifPresent(history -> history.markAsExpired());
        
        // Save new history
        historyRepository.save(newHistory);
    }

    @Transactional
    public boolean validateOtp(@Email String email, String providedOtp) {
        if (email == null) {
            return false;
        }
        
        String normalizedEmail = email.toLowerCase();
        Optional<OtpHistory> historyOpt = historyRepository.findByEmailAndStatus(normalizedEmail, OtpStatus.ACTIVE);
        
        if (historyOpt.isEmpty()) {
            return false;
        }

        OtpHistory history = historyOpt.get();
        history.incrementAttempts();
        
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

    @Transactional
    public boolean revokeOtp(@Email String email, String reason) {
        if (email == null) {
            return false;
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