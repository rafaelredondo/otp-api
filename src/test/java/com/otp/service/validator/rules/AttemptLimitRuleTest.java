package com.otp.service.validator.rules;

import com.otp.model.OtpAttempt;
import com.otp.repository.OtpAttemptRepository;
import com.otp.service.validator.ValidationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

public class AttemptLimitRuleTest {
    private AttemptLimitRule rule;
    private OtpAttemptRepository mockRepository;

    @BeforeEach
    void setUp() {
        mockRepository = Mockito.mock(OtpAttemptRepository.class);
        rule = new AttemptLimitRule(mockRepository);
    }

    @Test
    @DisplayName("Should allow when under max attempts")
    void shouldAllowWhenUnderMaxAttempts() {
        // Given
        String email = "test@example.com";
        ValidationContext context = new ValidationContext(email, "123456", "123456", Instant.now());
        
        Mockito.when(mockRepository.findRecentAttempts(
            Mockito.anyString(), 
            Mockito.any(Instant.class)))
            .thenReturn(List.of(
                new OtpAttempt(email, Instant.now()),
                new OtpAttempt(email, Instant.now())
            ));

        // When/Then
        assertTrue(rule.validate(context));
    }

    @Test
    @DisplayName("Should block when exceeds max attempts")
    void shouldBlockWhenExceedsMaxAttempts() {
        // Given
        String email = "test@example.com";
        ValidationContext context = new ValidationContext(email, "123456", "123456", Instant.now());
        
        Mockito.when(mockRepository.findRecentAttempts(
            Mockito.anyString(), 
            Mockito.any(Instant.class)))
            .thenReturn(List.of(
                new OtpAttempt(email, Instant.now()),
                new OtpAttempt(email, Instant.now()),
                new OtpAttempt(email, Instant.now()),
                new OtpAttempt(email, Instant.now()),
                new OtpAttempt(email, Instant.now()),
                new OtpAttempt(email, Instant.now())
            ));

        // When/Then
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should handle null email")
    void shouldHandleNullEmail() {
        // Given
        ValidationContext context = new ValidationContext(null, "123456", "123456", Instant.now());

        // When/Then
        assertFalse(rule.validate(context));
    }

    @Test
    @DisplayName("Should reset attempts after window")
    void shouldResetAttemptsAfterWindow() {
        // Given
        String email = "test@example.com";
        ValidationContext context = new ValidationContext(email, "123456", "123456", Instant.now());
        
        // Mock old attempts (outside window)
        Mockito.when(mockRepository.findRecentAttempts(
            Mockito.anyString(), 
            Mockito.any(Instant.class)))
            .thenReturn(new ArrayList<>());

        // When/Then
        assertTrue(rule.validate(context));
    }
} 