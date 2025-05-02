package com.otp.service.validator.rules;

import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

public class OtpLengthRuleTest {
    private final OtpLengthRule rule = new OtpLengthRule();

    @Test
    @DisplayName("Should validate when OTP length is 6")
    public void shouldValidateCorrectLength() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "123456",
            Instant.now()
        );
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTP is too short")
    public void shouldFailWhenTooShort() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "12345",
            "123456",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTP is too long")
    public void shouldFailWhenTooLong() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "1234567",
            "123456",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }
} 