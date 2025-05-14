package com.otp.service.validator.rules;

import com.otp.config.OtpConfig;
import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.time.Instant;

public class OtpExpirationRuleTest {
    private OtpExpirationRule rule;
    private OtpConfig mockOtpConfig;
    private OtpConfig.Validation mockValidationConfig;
    
    @BeforeEach
    void setUp() {
        mockOtpConfig = Mockito.mock(OtpConfig.class);
        mockValidationConfig = Mockito.mock(OtpConfig.Validation.class);
        
        when(mockOtpConfig.getValidation()).thenReturn(mockValidationConfig);
        when(mockValidationConfig.getExpirationMinutes()).thenReturn(30);
        
        rule = new OtpExpirationRule(mockOtpConfig);
    }

    @Test
    @DisplayName("Should validate when OTP is not expired")
    public void shouldValidateNonExpiredOtp() {
        // Given
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "encrypted_123456",
            Instant.now()
        );
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should fail when OTP is expired")
    public void shouldFailWhenExpired() {
        // Given
        int expirationMinutes = 30;
        when(mockValidationConfig.getExpirationMinutes()).thenReturn(expirationMinutes);
        
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "encrypted_123456",
            Instant.now().minusSeconds(expirationMinutes * 60 + 1) // expiration time + 1 second ago
        );
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should fail when timestamp is null")
    public void shouldFailWhenTimestampIsNull() {
        // Given
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "encrypted_123456",
            null
        );
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertFalse(result);
    }
} 