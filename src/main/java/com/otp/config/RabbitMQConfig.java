package com.otp.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test") // Only active when not in test profile
public class RabbitMQConfig {
    public static final String OTP_NOTIFICATION_QUEUE = "otp-notification-queue";
    public static final String OTP_NOTIFICATION_DLQ = "otp-notification-dlq";
    
    @Bean
    public Queue otpNotificationQueue() {
        return new Queue(OTP_NOTIFICATION_QUEUE, true);
    }
    
    @Bean
    public Queue otpNotificationDLQ() {
        return new Queue(OTP_NOTIFICATION_DLQ, true);
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
} 