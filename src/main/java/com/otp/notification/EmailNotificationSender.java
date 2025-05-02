package com.otp.notification;

import com.otp.service.EmailService;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {
    private final EmailService emailService;

    public EmailNotificationSender(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public boolean send(String to, String message) {
        emailService.sendEmail(to, message, "OTP Code");
        return true;
    }
} 