package com.otp.notification;

import org.springframework.stereotype.Component;

@Component
public class NotificationSenderFactory {
    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;

    public NotificationSenderFactory(EmailNotificationSender emailSender, SmsNotificationSender smsSender) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    public NotificationSender getSender(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailSender;
            case SMS -> smsSender;
            default -> throw new IllegalArgumentException("Canal não suportado: " + channel);
        };
    }
} 