package com.otp.notification;

import org.springframework.stereotype.Component;

@Component
public class SmsNotificationSender implements NotificationSender {
    @Override
    public boolean send(String to, String message) {
        System.out.println("SMS enviado para " + to + ": " + message);
        return true;
    }
} 