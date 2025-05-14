package com.otp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.mockito.Mockito;

@TestConfiguration
@Profile("test")
public class TestRabbitListenerConfig {
    
    @Bean
    @Primary
    public RabbitListenerAnnotationBeanPostProcessor mockRabbitListenerAnnotationBeanPostProcessor() {
        // Retorna um mock que não processa anotações @RabbitListener
        return Mockito.mock(RabbitListenerAnnotationBeanPostProcessor.class);
    }
} 