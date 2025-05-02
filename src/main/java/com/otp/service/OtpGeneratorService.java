package com.otp.service;

import com.otp.model.OtpResponse;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.Email;
import java.security.SecureRandom;

@Service
public class OtpGeneratorService {
    private static final int OTP_LENGTH = 6;
    private static final String DIGITS = "0123456789";
    private final SecureRandom random = new SecureRandom();
    private final OtpNotificationService notificationService;

    public OtpGeneratorService(OtpNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public OtpResponse generateOtp(@Email String email) {
        String otpValue = generateRandomOtp();
        boolean delivered = notificationService.sendOtpNotification(email, otpValue);
        return new OtpResponse(otpValue, delivered);
    }

    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            int randomIndex = Math.abs(random.nextInt()) % DIGITS.length();
            otp.append(DIGITS.charAt(randomIndex));
        }
        return otp.toString();
    }
} 