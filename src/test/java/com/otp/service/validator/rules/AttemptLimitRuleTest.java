package com.otp.service.validator.rules;

import com.otp.config.OtpConfig;
import com.otp.exception.TooManyAttemptsException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AttemptLimitRuleTest {
    private AttemptLimitRule rule;
    private OtpAttemptRepository mockAttemptRepository;
    private OtpConfig mockOtpConfig;
    private OtpConfig.Validation mockValidationConfig;

    @BeforeEach
    void setUp() {
        mockAttemptRepository = Mockito.mock(OtpAttemptRepository.class);
        mockOtpConfig = Mockito.mock(OtpConfig.class);
        mockValidationConfig = Mockito.mock(OtpConfig.Validation.class);
        
        when(mockOtpConfig.getValidation()).thenReturn(mockValidationConfig);
        when(mockValidationConfig.getMaxAttempts()).thenReturn(5);
        when(mockValidationConfig.getWindowMinutes()).thenReturn(15);
        
        when(mockAttemptRepository.save(any(OtpAttempt.class)))
            .thenAnswer(i -> i.getArgument(0));
            
        rule = new AttemptLimitRule(mockAttemptRepository, mockOtpConfig);
    }

    @Test
    @DisplayName("Should validate when attempts are below limit")
    void shouldValidateWhenAttemptsAreBelowLimit() {
        // Given
        String email = "test@example.com";
        Instant now = Instant.now();
        List<OtpAttempt> recentAttempts = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            recentAttempts.add(new OtpAttempt(email, now.minusSeconds(i * 60)));
        }
        
        when(mockAttemptRepository.findRecentAttempts(anyString(), any(Instant.class)))
            .thenReturn(recentAttempts);
            
        ValidationContext context = new ValidationContext(email, "123456", "encrypted_123456", now);
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should throw TooManyAttemptsException when limit is exceeded")
    void shouldThrowExceptionWhenLimitIsExceeded() {
        // Given
        String email = "test@example.com";
        Instant now = Instant.now();
        List<OtpAttempt> recentAttempts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            recentAttempts.add(new OtpAttempt(email, now.minusSeconds(i * 60)));
        }
        
        when(mockAttemptRepository.findRecentAttempts(anyString(), any(Instant.class)))
            .thenReturn(recentAttempts);
            
        ValidationContext context = new ValidationContext(email, "123456", "encrypted_123456", now);
        
        // When/Then
        assertThrows(TooManyAttemptsException.class, () -> {
            rule.validate(context);
        });
    }
    
    @Test
    @DisplayName("Should return false when email is null")
    void shouldReturnFalseWhenEmailIsNull() {
        // Given
        ValidationContext context = new ValidationContext(null, "123456", "encrypted_123456", Instant.now());
        
        // When
        boolean result = rule.validate(context);
        
        // Then
        assertFalse(result);
    }
} 