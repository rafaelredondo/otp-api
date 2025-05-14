package com.otp.service;

import com.otp.model.OtpNotificationMessage;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.otp.config.RabbitMQConfig;
import com.otp.notification.NotificationSenderFactory;
import com.otp.notification.NotificationChannel;
import com.otp.notification.NotificationSender;
import com.otp.notification.MessageQueueService;

public interface OtpNotificationService {
    boolean sendOtpNotification(String email, String otp);
    void processOtpNotification(OtpNotificationMessage message);
    void recoverOtpNotification(Exception e, OtpNotificationMessage message);
}

@Service
class DefaultOtpNotificationService implements OtpNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultOtpNotificationService.class);
    private final NotificationSenderFactory senderFactory;
    private final MessageQueueService messageQueueService;

    public DefaultOtpNotificationService(MessageQueueService messageQueueService, NotificationSenderFactory senderFactory) {
        this.messageQueueService = messageQueueService;
        this.senderFactory = senderFactory;
    }

    @Override
    public boolean sendOtpNotification(String email, String otp) {
        NotificationSender sender = senderFactory.getSender(NotificationChannel.EMAIL);
        return sender.send(email, otp);
    }

    @Override
    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processOtpNotification(OtpNotificationMessage message) {
        messageQueueService.sendToQueue(RabbitMQConfig.OTP_NOTIFICATION_QUEUE, message);
        logger.info("OTP notification sent successfully to: {}", message.email());
    }

    @Override
    @Recover
    public void recoverOtpNotification(Exception e, OtpNotificationMessage message) {
        logger.error("All retries failed for email: {}. Moving to DLQ", message.email(), e);
        messageQueueService.sendToDLQ(RabbitMQConfig.OTP_NOTIFICATION_DLQ, message);
    }
} 