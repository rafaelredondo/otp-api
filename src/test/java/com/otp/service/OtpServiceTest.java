package com.otp.service;

import com.otp.model.OtpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;   
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {
    private OtpService otpService;
    
    @Mock
    private OtpGeneratorService generatorService;
    @Mock
    private OtpValidatorService validatorService;
    @Mock
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        otpService = new OtpService(generatorService, validatorService, encryptionService);
    }

    @Test
    @DisplayName("Should generate and validate OTP")
    void shouldGenerateAndValidateOtp() {
        // Given
        String email = "user@example.com";
        String otp = "123456";
        OtpResponse mockResponse = new OtpResponse(otp, true);
        
        Mockito.when(generatorService.generateOtp(email)).thenReturn(mockResponse);
        Mockito.when(validatorService.validateOtp(email, otp)).thenReturn(true);
        Mockito.when(encryptionService.encrypt(otp)).thenReturn("encrypted_" + otp);

        // When
        OtpResponse response = otpService.generateOtp(email);

        // Then
        assertNotNull(response);
        assertTrue(response.delivered());
        assertEquals(otp, response.otp());
        
        // Validate OTP
        assertTrue(otpService.validateOtp(email, otp));
        
        // Verify interactions
        verify(generatorService).generateOtp(email);
        verify(encryptionService).encrypt(otp);
    }

    @Test
    @DisplayName("Should revoke OTP with reason")
    void shouldRevokeOtpWithReason() {
        // Given
        String email = "user@example.com";
        String reason = "Security concern";
        
        Mockito.when(validatorService.revokeOtp(email.toLowerCase(), reason)).thenReturn(true);

        // When
        boolean revoked = otpService.revokeOtp(email, reason);

        // Then
        assertTrue(revoked);
        verify(validatorService).revokeOtp(email.toLowerCase(), reason);
    }

    @Test
    @DisplayName("Should handle case insensitive email")
    public void shouldHandleCaseInsensitiveEmail() {
        // Given
        String email1 = "User@Example.com";
        String otp = "123456";
        
        Mockito.when(validatorService.validateOtp(email1, otp))
               .thenReturn(true);

        // When
        boolean isValid = otpService.validateOtp(email1, otp);

        // Then
        assertTrue(isValid);
        verify(validatorService).validateOtp(email1, otp);
    }

    @Test
    @DisplayName("Should not validate OTP twice")
    public void shouldNotValidateOtpTwice() {
        // Given
        String email = "user@example.com";
        String otp = "123456";
        
        Mockito.when(validatorService.validateOtp(email.toLowerCase(), otp))
               .thenReturn(true)
               .thenReturn(false);

        // When
        boolean firstValidation = otpService.validateOtp(email, otp);
        boolean secondValidation = otpService.validateOtp(email, otp);

        // Then
        assertTrue(firstValidation);
        assertFalse(secondValidation);
    }

    @Test
    @DisplayName("Should generate and validate encrypted OTP")
    void shouldGenerateAndValidateEncryptedOtp() {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        String encryptedOtp = "encrypted_123456";
        
        OtpResponse mockResponse = new OtpResponse(otp, true);
        Mockito.when(generatorService.generateOtp(email)).thenReturn(mockResponse);
        Mockito.when(encryptionService.encrypt(otp)).thenReturn(encryptedOtp);
        Mockito.when(validatorService.validateOtp(email, otp)).thenReturn(true);

        // When
        OtpResponse response = otpService.generateOtp(email);
        boolean isValid = otpService.validateOtp(email, otp);

        // Then
        assertTrue(response.delivered());
        assertTrue(isValid);
        verify(encryptionService).encrypt(otp);
    }
} 