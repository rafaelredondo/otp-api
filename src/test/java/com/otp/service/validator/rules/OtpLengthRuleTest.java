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

public class OtpLengthRuleTest {
    private OtpLengthRule rule;
    private OtpConfig mockOtpConfig;
    private OtpConfig.Generation mockGenerationConfig;
    
    @BeforeEach
    void setUp() {
        mockOtpConfig = Mockito.mock(OtpConfig.class);
        mockGenerationConfig = Mockito.mock(OtpConfig.Generation.class);
        
        when(mockOtpConfig.getGeneration()).thenReturn(mockGenerationConfig);
        when(mockGenerationConfig.getLength()).thenReturn(6);
        
        rule = new OtpLengthRule(mockOtpConfig);
    }

    @Test
    @DisplayName("Should validate when OTP length matches configured length")
    public void shouldValidateCorrectLength() {
        // Given
        when(mockGenerationConfig.getLength()).thenReturn(6);
        
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
    @DisplayName("Should fail when OTP is too short")
    public void shouldFailWhenTooShort() {
        // Given
        when(mockGenerationConfig.getLength()).thenReturn(6);
        
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "12345",
            "encrypted_123456",
            Instant.now()
        );
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should fail when OTP is too long")
    public void shouldFailWhenTooLong() {
        // Given
        when(mockGenerationConfig.getLength()).thenReturn(6);
        
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "1234567",
            "encrypted_123456",
            Instant.now()
        );
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertFalse(result);
    }
    
    @Test
    @DisplayName("Should validate with different configured length")
    public void shouldValidateWithDifferentConfiguredLength() {
        // Given
        when(mockGenerationConfig.getLength()).thenReturn(4);
        
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "1234",
            "encrypted_1234",
            Instant.now()
        );
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertTrue(result);
    }
} 