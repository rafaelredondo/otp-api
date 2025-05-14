package com.otp.config;

import com.otp.notification.NotificationSender;
import com.otp.notification.NotificationSenderFactory;
import com.otp.notification.NotificationChannel;
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
    public NotificationSenderFactory mockNotificationSenderFactory() {
        NotificationSenderFactory factory = Mockito.mock(NotificationSenderFactory.class);
        NotificationSender mockSender = Mockito.mock(NotificationSender.class);
        
        // Configure mock sender to always return true
        Mockito.when(mockSender.send(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        
        // Configure factory to return mock sender for any channel
        Mockito.when(factory.getSender(Mockito.any(NotificationChannel.class))).thenReturn(mockSender);
        
        return factory;
    }
    
    @Bean
    @Primary
    public NotificationSender mockNotificationSender() {
        NotificationSender sender = Mockito.mock(NotificationSender.class);
        Mockito.when(sender.send(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        return sender;
    }
} 