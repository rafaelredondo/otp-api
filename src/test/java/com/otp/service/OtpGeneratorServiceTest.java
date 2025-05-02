package com.otp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import com.otp.model.OtpResponse;
import org.mockito.Mock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class OtpGeneratorServiceTest {
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private EmailService emailService;
    @Mock
    private OtpNotificationService notificationService;

    private OtpGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        generatorService = new OtpGeneratorService(notificationService);
    }

    @Test
    @DisplayName("Should generate valid OTP")
    public void shouldGenerateValidOtp() {
        // Given
        String email = "user@example.com";
        Mockito.when(notificationService.sendOtpNotification(
            ArgumentMatchers.anyString(), 
            ArgumentMatchers.anyString()
        )).thenReturn(true);

        // When
        OtpResponse response = generatorService.generateOtp(email);

        // Then
        assertNotNull(response);
        assertNotNull(response.otp());
        assertTrue(response.delivered());
        assertEquals(6, response.otp().length());
        assertTrue(response.otp().matches("\\d{6}")); // Verify it's 6 digits
        verify(notificationService).sendOtpNotification(email, response.otp());
    }

    @Test
    @DisplayName("Should generate different OTPs for consecutive calls")
    public void shouldGenerateDifferentOtps() {
        // Given
        String email = "user@example.com";
        Mockito.when(notificationService.sendOtpNotification(
            ArgumentMatchers.anyString(), 
            ArgumentMatchers.anyString()
        )).thenReturn(true);

        // When
        String firstOtp = generatorService.generateOtp(email).otp();
        String secondOtp = generatorService.generateOtp(email).otp();

        // Then
        assertNotEquals(firstOtp, secondOtp);
        verify(notificationService, times(2)).sendOtpNotification(
            ArgumentMatchers.eq(email), 
            ArgumentMatchers.anyString()
        );
    }
} 