package com.otp.service;

import com.otp.config.OtpConfig;
import com.otp.model.OtpResponse;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.Email;
import java.security.SecureRandom;

/**
 * Interface for OTP generation service
 */
public interface OtpGeneratorService {
    OtpResponse generateOtp(@Email String email);
}

/**
 * Default implementation of OtpGeneratorService
 */
@Service
class DefaultOtpGeneratorService implements OtpGeneratorService {
    private static final String DIGITS = "0123456789";
    private final SecureRandom random = new SecureRandom();
    private final OtpNotificationService notificationService;
    private final OtpConfig otpConfig;

    public DefaultOtpGeneratorService(OtpNotificationService notificationService, OtpConfig otpConfig) {
        this.notificationService = notificationService;
        this.otpConfig = otpConfig;
    }

    @Override
    public OtpResponse generateOtp(@Email String email) {
        String otpValue = generateRandomOtp();
        boolean delivered = notificationService.sendOtpNotification(email, otpValue);
        return new OtpResponse(otpValue, delivered);
    }

    private String generateRandomOtp() {
        int otpLength = otpConfig.getGeneration().getLength();
        String prefix = otpConfig.getGeneration().getPrefix();
        
        StringBuilder otp = new StringBuilder(otpLength);
        
        // Add configured prefix if any
        otp.append(prefix);
        
        // Generate random digits for the remaining length
        int remainingLength = otpLength - prefix.length();
        
        for (int i = 0; i < remainingLength; i++) {
            int randomIndex = Math.abs(random.nextInt()) % DIGITS.length();
            otp.append(DIGITS.charAt(randomIndex));
        }
        
        return otp.toString();
    }
} 