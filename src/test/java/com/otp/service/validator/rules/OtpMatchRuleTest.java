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

public class OtpMatchRuleTest {
    private final EncryptionService encryptionService = Mockito.mock(EncryptionService.class);
    private final OtpMatchRule rule = new OtpMatchRule(encryptionService);

    @BeforeEach
    void setUp() {
        Mockito.when(encryptionService.decrypt(ArgumentMatchers.any()))
               .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    @DisplayName("Should validate when OTPs match")
    public void shouldValidateMatchingOtps() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "123456",
            Instant.now()
        );
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTPs don't match")
    public void shouldFailWhenOtpsDontMatch() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "654321",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTPs have different lengths")
    public void shouldFailWhenOtpsHaveDifferentLengths() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "12345",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }
} 