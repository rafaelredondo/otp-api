package com.otp.config;

import com.otp.notification.EmailNotificationSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.mockito.Mockito;

@TestConfiguration
@Profile("test")
public class TestConfig {
    
    @Bean
    @Primary
    public EmailNotificationSender mockEmailNotificationSender() {
        EmailNotificationSender sender = Mockito.mock(EmailNotificationSender.class);
        Mockito.when(sender.send(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        return sender;
    }
} 