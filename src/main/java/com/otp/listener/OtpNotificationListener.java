package com.otp.listener;

import com.otp.model.OtpNotificationMessage;
import com.otp.service.OtpNotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import com.otp.config.RabbitMQConfig;

@Component
@Profile("!test") // Only active when not in test profile
public class OtpNotificationListener {
    private final OtpNotificationService notificationService;

    public OtpNotificationListener(OtpNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.OTP_NOTIFICATION_QUEUE)
    public void processOtpNotification(OtpNotificationMessage message) {
        notificationService.processOtpNotification(message);
    }
} 