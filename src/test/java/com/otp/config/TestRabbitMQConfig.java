package com.otp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.mockito.Mockito;

@TestConfiguration
@Profile("test")
public class TestRabbitMQConfig {
    
    @Bean
    @Primary
    public ConnectionFactory mockConnectionFactory() {
        return Mockito.mock(ConnectionFactory.class);
    }
    
    @Bean
    @Primary
    public RabbitTemplate mockRabbitTemplate() {
        return Mockito.mock(RabbitTemplate.class);
    }
    
    @Bean
    @Primary
    public SimpleMessageListenerContainer mockMessageListenerContainer() {
        SimpleMessageListenerContainer container = Mockito.mock(SimpleMessageListenerContainer.class);
        Mockito.doNothing().when(container).start();
        Mockito.doNothing().when(container).stop();
        return container;
    }
} 