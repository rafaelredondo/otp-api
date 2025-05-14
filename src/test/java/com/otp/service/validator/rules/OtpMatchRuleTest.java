package com.otp.service.validator.rules;

import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import org.mockito.Mockito;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import com.otp.service.EncryptionService;
import com.otp.service.DefaultEncryptionService;

public class OtpMatchRuleTest {
    private EncryptionService encryptionService;
    private OtpMatchRule rule;

    @BeforeEach
    void setUp() {
        encryptionService = Mockito.mock(EncryptionService.class);
        rule = new OtpMatchRule(encryptionService);
    }

    @Test
    @DisplayName("Should validate when OTPs match")
    public void shouldValidateMatchingOtps() {
        // Arrange
        String otp = "123456";
        Mockito.when(encryptionService.encrypt(otp)).thenReturn("encrypted_123456");
        ValidationContext context = new ValidationContext(
            "test@example.com",
            otp,
            "encrypted_123456",
            Instant.now()
        );
        // Act & Assert
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTPs don't match")
    public void shouldFailWhenOtpsDontMatch() {
        Mockito.when(encryptionService.encrypt("123456")).thenReturn("encrypted_123456");
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "encrypted_654321",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTPs have different lengths")
    public void shouldFailWhenOtpsHaveDifferentLengths() {
        Mockito.when(encryptionService.encrypt("123456")).thenReturn("encrypted_123456");
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "encrypted_12345",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }
} 