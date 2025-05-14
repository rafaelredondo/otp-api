package com.otp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.otp.exception.OtpGenerationException;
import com.otp.exception.OtpValidationException;
import com.otp.model.OtpResponse;

@ExtendWith(MockitoExtension.class)
public class OtpServiceTest {
    
    private DefaultOtpService otpService;
    
    @Mock
    private OtpGeneratorService generatorService;
    
    @Mock
    private OtpValidatorService validatorService;
    
    @Mock
    private EncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        otpService = new DefaultOtpService(generatorService, validatorService, encryptionService);
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
        boolean isValid = otpService.validateOtp(email, otp);
        assertTrue(isValid);
        
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
        
        Mockito.when(validatorService.validateOtp(anyString(), anyString())).thenReturn(true);

        // When
        boolean isValid = otpService.validateOtp(email1, otp);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should not validate OTP twice")
    public void shouldNotValidateOtpTwice() {
        // Given
        String email = "user@example.com";
        String otp = "123456";
        
        Mockito.when(validatorService.validateOtp(anyString(), anyString()))
               .thenReturn(true)
               .thenThrow(new OtpValidationException("Invalid OTP"));

        // When
        boolean firstValidation = otpService.validateOtp(email, otp);
        
        // Then
        assertTrue(firstValidation);
        
        // Expect exception on second validation
        assertThrows(OtpValidationException.class, () -> {
            otpService.validateOtp(email, otp);
        });
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
    
    @Test
    @DisplayName("Should throw OtpGenerationException when delivery fails")
    void shouldThrowOtpGenerationExceptionWhenDeliveryFails() {
        // Given
        String email = "user@example.com";
        OtpResponse mockResponse = new OtpResponse("123456", false);
        
        Mockito.when(generatorService.generateOtp(email)).thenReturn(mockResponse);
        
        // When/Then
        assertThrows(OtpGenerationException.class, () -> {
            otpService.generateOtp(email);
        });
    }
    
    @Test
    @DisplayName("Should throw OtpValidationException for invalid OTP")
    void shouldThrowOtpValidationExceptionForInvalidOtp() {
        // Given
        String email = "user@example.com";
        String otp = "123456";
        
        Mockito.when(validatorService.validateOtp(anyString(), anyString()))
               .thenThrow(new OtpValidationException("Invalid OTP"));
        
        // When/Then
        assertThrows(OtpValidationException.class, () -> {
            otpService.validateOtp(email, otp);
        });
    }
} 