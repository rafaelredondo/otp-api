package com.otp.notification;

import com.otp.model.OtpNotificationMessage;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public interface MessageQueueService {
    void sendToQueue(String queueName, OtpNotificationMessage message);
    void sendToDLQ(String queueName, OtpNotificationMessage message);
}

@Component
class RabbitMQMessageQueueService implements MessageQueueService {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQMessageQueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void sendToQueue(String queueName, OtpNotificationMessage message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }

    @Override
    public void sendToDLQ(String queueName, OtpNotificationMessage message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }
} 