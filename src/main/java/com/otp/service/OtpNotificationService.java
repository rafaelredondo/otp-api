package com.otp.service;

import com.otp.model.OtpNotificationMessage;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.otp.config.RabbitMQConfig;
import com.otp.notification.NotificationSenderFactory;
import com.otp.notification.NotificationChannel;
import com.otp.notification.NotificationSender;

@Service
public class OtpNotificationService {
    private static final Logger logger = LoggerFactory.getLogger(OtpNotificationService.class);
    private final NotificationSenderFactory senderFactory;
    private final RabbitTemplate rabbitTemplate;

    public OtpNotificationService(RabbitTemplate rabbitTemplate, NotificationSenderFactory senderFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.senderFactory = senderFactory;
    }

    public boolean sendOtpNotification(String email, String otp) {
        NotificationSender sender = senderFactory.getSender(NotificationChannel.EMAIL);
        return sender.send(email, otp);
    }

    @Retryable(
        value = {Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processOtpNotification(OtpNotificationMessage message) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.OTP_NOTIFICATION_QUEUE, message);
        logger.info("OTP notification sent successfully to: {}", message.email());
    }

    @Recover
    public void recoverOtpNotification(Exception e, OtpNotificationMessage message) {
        logger.error("All retries failed for email: {}. Moving to DLQ", message.email(), e);
        rabbitTemplate.convertAndSend(RabbitMQConfig.OTP_NOTIFICATION_DLQ, message);
    }
} 