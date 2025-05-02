package com.otp.service;

import com.otp.model.OtpHistory;
import com.otp.model.OtpStatus;
import com.otp.repository.OtpHistoryRepository;
import com.otp.service.validator.OtpValidationRule;
import com.otp.service.validator.rules.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Optional;

public class OtpValidatorServiceTest {
    private OtpValidatorService validatorService;
    private OtpHistoryRepository mockHistoryRepository;

    @BeforeEach
    void setUp() {
        mockHistoryRepository = Mockito.mock(OtpHistoryRepository.class);
        EncryptionService encryptionService = Mockito.mock(EncryptionService.class);
        
        // Configure o mock do EncryptionService
        Mockito.when(encryptionService.encrypt(any()))
               .thenAnswer(i -> "encrypted_" + i.getArgument(0));
        Mockito.when(encryptionService.decrypt(any()))
               .thenAnswer(i -> i.getArgument(0).toString().replace("encrypted_", ""));
        
        List<OtpValidationRule> rules = List.of(
            new NotNullRule(),
            new OtpLengthRule(),
            new OtpExpirationRule(),
            new OtpMatchRule(encryptionService)
        );
        validatorService = new OtpValidatorService(rules, mockHistoryRepository);
    }

    @Test
    @DisplayName("Should store new OTP and invalidate previous one")
    void shouldStoreNewOtp() {
        // Given
        String email = "test@example.com";
        String encryptedOtp = "encrypted_123456";
        OtpHistory activeOtp = new OtpHistory(email, "encrypted_000000");
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(activeOtp));
        Mockito.when(mockHistoryRepository.save(any(OtpHistory.class)))
            .thenAnswer(i -> i.getArgument(0));

        // When
        OtpHistory newHistory = new OtpHistory(email, encryptedOtp);
        validatorService.storeOtp(email, newHistory);

        // Then
        Mockito.verify(mockHistoryRepository).save(any(OtpHistory.class));
        assertEquals(OtpStatus.EXPIRED, activeOtp.getStatus());
    }

    @Test
    @DisplayName("Should validate correct OTP")
    void shouldValidateCorrectOtp() {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        OtpHistory history = new OtpHistory(email, "encrypted_123456");
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(history));

        // When
        boolean result = validatorService.validateOtp(email, otp);

        // Then
        assertTrue(result);
        assertEquals(OtpStatus.USED, history.getStatus());
        assertNotNull(history.getUsedAt());
    }

    @Test
    @DisplayName("Should revoke active OTP")
    void shouldRevokeActiveOtp() {
        // Given
        String email = "test@example.com";
        String reason = "Security concern";
        OtpHistory history = new OtpHistory(email, "encrypted_123456");
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(history));

        // When
        boolean result = validatorService.revokeOtp(email, reason);

        // Then
        assertTrue(result);
        assertEquals(OtpStatus.REVOKED, history.getStatus());
        assertEquals(reason, history.getRevokedReason());
        assertNotNull(history.getRevokedAt());
    }

    @Test
    @DisplayName("Should track validation attempts")
    void shouldTrackValidationAttempts() {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        OtpHistory history = new OtpHistory(email, "encrypted_123456");
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(history));

        // When
        validatorService.validateOtp(email, "wrong");
        validatorService.validateOtp(email, "wrong");
        validatorService.validateOtp(email, otp);

        // Then
        assertEquals(3, history.getAttemptCount());
        assertEquals(OtpStatus.USED, history.getStatus());
    }

    @Test
    @DisplayName("Should handle case insensitive email")
    void shouldHandleCaseInsensitiveEmail() {
        // Given
        String email1 = "User@Example.com";
        String email2 = "user@example.com";
        String otp = "123456";
        OtpHistory history = new OtpHistory(email2, "encrypted_123456");
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email2, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(history));

        // When
        boolean result = validatorService.validateOtp(email1, otp);

        // Then
        assertTrue(result);
    }
} 