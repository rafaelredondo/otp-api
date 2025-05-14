package com.otp.notification;

import org.springframework.stereotype.Component;

public interface NotificationSenderFactory {
    NotificationSender getSender(NotificationChannel channel);
}

@Component
class DefaultNotificationSenderFactory implements NotificationSenderFactory {
    private final EmailNotificationSender emailSender;

    public DefaultNotificationSenderFactory(EmailNotificationSender emailSender) {
        this.emailSender = emailSender;
    }

    @Override
    public NotificationSender getSender(NotificationChannel channel) {
        if (channel == NotificationChannel.EMAIL) {
            return emailSender;
        }
        throw new IllegalArgumentException("Canal não suportado: " + channel);
    }
} 