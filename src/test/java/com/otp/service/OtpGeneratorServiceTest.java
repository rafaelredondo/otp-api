package com.otp.service;

import com.otp.config.OtpConfig;
import com.otp.model.OtpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OtpGeneratorServiceTest {
    private DefaultOtpGeneratorService generatorService;
    
    @Mock
    private OtpNotificationService mockNotificationService;
    
    @Mock
    private OtpConfig mockOtpConfig;
    
    @Mock
    private OtpConfig.Generation mockGenerationConfig;

    @BeforeEach
    void setUp() {
        when(mockOtpConfig.getGeneration()).thenReturn(mockGenerationConfig);
        when(mockGenerationConfig.getLength()).thenReturn(6);
        when(mockGenerationConfig.getPrefix()).thenReturn("");
        
        when(mockNotificationService.sendOtpNotification(anyString(), anyString())).thenReturn(true);
        
        generatorService = new DefaultOtpGeneratorService(mockNotificationService, mockOtpConfig);
    }

    @Test
    @DisplayName("Should generate OTP with correct length")
    void shouldGenerateOtpWithCorrectLength() {
        // Given
        String email = "test@example.com";
        when(mockGenerationConfig.getLength()).thenReturn(6);

        // When
        OtpResponse response = generatorService.generateOtp(email);

        // Then
        assertNotNull(response);
        assertEquals(6, response.otp().length());
        assertTrue(response.delivered());
    }

    @Test
    @DisplayName("Should generate OTP with custom length")
    void shouldGenerateOtpWithCustomLength() {
        // Given
        String email = "test@example.com";
        when(mockGenerationConfig.getLength()).thenReturn(8);

        // When
        OtpResponse response = generatorService.generateOtp(email);

        // Then
        assertNotNull(response);
        assertEquals(8, response.otp().length());
    }

    @Test
    @DisplayName("Should include prefix in OTP")
    void shouldIncludePrefixInOtp() {
        // Given
        String email = "test@example.com";
        when(mockGenerationConfig.getLength()).thenReturn(8);
        when(mockGenerationConfig.getPrefix()).thenReturn("ABC");

        // When
        OtpResponse response = generatorService.generateOtp(email);

        // Then
        assertNotNull(response);
        assertEquals(8, response.otp().length());
        assertTrue(response.otp().startsWith("ABC"));
    }

    @Test
    @DisplayName("Should set delivered to false when notification fails")
    void shouldSetDeliveredToFalseWhenNotificationFails() {
        // Given
        String email = "test@example.com";
        when(mockNotificationService.sendOtpNotification(anyString(), anyString())).thenReturn(false);

        // When
        OtpResponse response = generatorService.generateOtp(email);

        // Then
        assertNotNull(response);
        assertFalse(response.delivered());
    }
} 