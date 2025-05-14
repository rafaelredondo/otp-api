package com.otp.service;

import com.otp.model.OtpNotificationMessage;
import com.otp.notification.NotificationSenderFactory;
import com.otp.notification.NotificationSender;
import com.otp.notification.NotificationChannel;
import com.otp.notification.MessageQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private MessageQueueService messageQueueService;

    private DefaultOtpNotificationService otpNotificationService;

    @BeforeEach
    void setUp() {
        lenient().when(senderFactory.getSender(NotificationChannel.EMAIL)).thenReturn(notificationSender);
        otpNotificationService = new DefaultOtpNotificationService(messageQueueService, senderFactory);
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
        doNothing().when(messageQueueService).sendToQueue(anyString(), eq(message));

        otpNotificationService.processOtpNotification(message);

        verify(messageQueueService).sendToQueue(anyString(), eq(message));
    }

    @Test
    void shouldMoveToDLQAfterMaxRetries() {
        OtpNotificationMessage message = new OtpNotificationMessage("user@email.com", "123456", 3);
        Exception ex = new Exception("Falha");

        doNothing().when(messageQueueService).sendToDLQ(anyString(), eq(message));

        otpNotificationService.recoverOtpNotification(ex, message);

        verify(messageQueueService).sendToDLQ(anyString(), eq(message));
    }
} 