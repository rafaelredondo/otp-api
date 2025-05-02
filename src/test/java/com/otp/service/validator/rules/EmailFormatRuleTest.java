package com.otp.service.validator.rules;

import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

public class EmailFormatRuleTest {
    private final EmailFormatRule rule = new EmailFormatRule();

    @Test
    @DisplayName("Should validate correct email format")
    public void shouldValidateCorrectEmail() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "123456",
            Instant.now()
        );
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail with invalid email format")
    public void shouldFailWithInvalidEmail() {
        ValidationContext context = new ValidationContext(
            "invalid-email",
            "123456",
            "123456",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail with null email")
    public void shouldFailWithNullEmail() {
        ValidationContext context = new ValidationContext(
            null,
            "123456",
            "123456",
            Instant.now()
        );
        assertFalse(rule.validate(context));
    }
} 