package com.otp.notification;

import com.otp.model.OtpNotificationMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class MockMessageQueueService implements MessageQueueService {
    @Override
    public void sendToQueue(String queueName, OtpNotificationMessage message) {
        // No-op for testing
    }

    @Override
    public void sendToDLQ(String queueName, OtpNotificationMessage message) {
        // No-op for testing
    }
} 