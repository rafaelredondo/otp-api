package com.otp.service;

import com.otp.model.OtpNotificationMessage;
import com.otp.notification.NotificationSenderFactory;
import com.otp.notification.NotificationSender;
import com.otp.notification.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OtpNotificationServiceTest {

    @Mock
    private NotificationSenderFactory senderFactory;

    @Mock
    private NotificationSender notificationSender;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OtpNotificationService otpNotificationService;

    @BeforeEach
    void setUp() {
        lenient().when(senderFactory.getSender(NotificationChannel.EMAIL)).thenReturn(notificationSender);
    }

    @Test
    void shouldSendOtpNotification() {
        when(notificationSender.send(anyString(), anyString())).thenReturn(true);

        boolean result = otpNotificationService.sendOtpNotification("user@email.com", "123456");
        assertTrue(result);
    }

    @Test
    void shouldHandleNotificationSenderError() {
        when(notificationSender.send(anyString(), anyString())).thenThrow(new RuntimeException("Falha no envio"));

        assertThrows(RuntimeException.class, () ->
            otpNotificationService.sendOtpNotification("user@email.com", "123456")
        );
    }

    @Test
    void shouldQueueNotificationSuccessfully() {
        OtpNotificationMessage message = new OtpNotificationMessage("user@email.com", "123456", 3);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), eq(message));

        otpNotificationService.processOtpNotification(message);

        verify(rabbitTemplate).convertAndSend(anyString(), eq(message));
    }

    @Test
    void shouldMoveToDLQAfterMaxRetries() {
        OtpNotificationMessage message = new OtpNotificationMessage("user@email.com", "123456", 3);
        Exception ex = new Exception("Falha");

        doNothing().when(rabbitTemplate).convertAndSend(anyString(), eq(message));

        otpNotificationService.recoverOtpNotification(ex, message);

        verify(rabbitTemplate).convertAndSend(anyString(), eq(message));
    }
} 