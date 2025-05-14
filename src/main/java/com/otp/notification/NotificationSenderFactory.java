package com.otp.notification;

import org.springframework.stereotype.Component;

public interface NotificationSenderFactory {
    NotificationSender getSender(NotificationChannel channel);
}

@Component
class DefaultNotificationSenderFactory implements NotificationSenderFactory {
    private final EmailNotificationSender emailSender;
    private final SmsNotificationSender smsSender;

    public DefaultNotificationSenderFactory(EmailNotificationSender emailSender, SmsNotificationSender smsSender) {
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    @Override
    public NotificationSender getSender(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailSender;
            case SMS -> smsSender;
            default -> throw new IllegalArgumentException("Canal não suportado: " + channel);
        };
    }
} 