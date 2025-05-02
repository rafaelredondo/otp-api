package com.otp.service.validator.rules;

import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

public class OtpExpirationRuleTest {
    private final OtpExpirationRule rule = new OtpExpirationRule();

    @Test
    @DisplayName("Should validate when OTP is not expired")
    public void shouldValidateNonExpiredOtp() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "123456",
            Instant.now()
        );
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should fail when OTP is expired")
    public void shouldFailWhenExpired() {
        ValidationContext context = new ValidationContext(
            "test@example.com",
            "123456",
            "123456",
            Instant.now().minusSeconds(301) // 5 minutes + 1 second ago
        );
        assertFalse(rule.validate(context));
    }
} 