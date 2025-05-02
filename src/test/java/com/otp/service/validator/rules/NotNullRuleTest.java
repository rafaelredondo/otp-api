package com.otp.service.validator.rules;

import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

public class NotNullRuleTest {
    private final NotNullRule rule = new NotNullRule();

    @Test
    @DisplayName("Should validate when email and OTP are not null")
    public void shouldValidateWhenNotNull() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "123456",
            Instant.now()
        );
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when email is null")
    public void shouldFailWhenEmailIsNull() {
        ValidationContext context = new ValidationContext(
            null,
            "123456",
            "123456",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTP is null")
    public void shouldFailWhenOtpIsNull() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            null,
            "123456",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }
} 