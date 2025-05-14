package com.otp.service;

import com.otp.config.OtpConfig;
import com.otp.exception.ResourceNotFoundException;
import com.otp.exception.TooManyAttemptsException;
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
    private OtpConfig mockOtpConfig;
    private OtpConfig.Validation mockValidationConfig;
    private OtpConfig.Generation mockGenerationConfig;

    @BeforeEach
    void setUp() {
        mockHistoryRepository = Mockito.mock(OtpHistoryRepository.class);
        mockOtpConfig = Mockito.mock(OtpConfig.class);
        
        mockValidationConfig = Mockito.mock(OtpConfig.Validation.class);
        mockGenerationConfig = Mockito.mock(OtpConfig.Generation.class);
        
        Mockito.when(mockOtpConfig.getValidation()).thenReturn(mockValidationConfig);
        Mockito.when(mockOtpConfig.getGeneration()).thenReturn(mockGenerationConfig);
        Mockito.when(mockValidationConfig.getMaxAttempts()).thenReturn(5);
        Mockito.when(mockValidationConfig.getWindowMinutes()).thenReturn(15);
        Mockito.when(mockValidationConfig.getExpirationMinutes()).thenReturn(30);
        Mockito.when(mockGenerationConfig.getLength()).thenReturn(6);
        Mockito.when(mockGenerationConfig.getPrefix()).thenReturn("");
        
        EncryptionService encryptionService = Mockito.mock(EncryptionService.class);
        
        // Configure o mock do EncryptionService
        Mockito.when(encryptionService.encrypt(any()))
               .thenAnswer(i -> "encrypted_" + i.getArgument(0));
        
        // Create rule instances with configuration
        List<OtpValidationRule> rules = List.of(
            new NotNullRule(),
            new OtpLengthRule(mockOtpConfig),
            new OtpExpirationRule(mockOtpConfig),
            new OtpMatchRule(encryptionService)
        );
        
        validatorService = new DefaultOtpValidatorService(rules, mockHistoryRepository, mockOtpConfig);
    }

    @Test
    @DisplayName("Should store new OTP and save expired OTP to database")
    void shouldStoreNewOtpAndSaveExpiredOtp() {
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
        Mockito.verify(mockHistoryRepository, Mockito.times(2)).save(any(OtpHistory.class)); // Verify save was called twice
        assertEquals(OtpStatus.EXPIRED, activeOtp.getStatus());
        assertEquals(OtpStatus.ACTIVE, newHistory.getStatus());
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

    @Test
    @DisplayName("Should throw ResourceNotFoundException when no active OTP")
    void shouldThrowResourceNotFoundExceptionWhenNoActiveOtp() {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.empty());

        // When/Then
        assertThrows(ResourceNotFoundException.class, () -> {
            validatorService.validateOtp(email, otp);
        });
    }
    
    @Test
    @DisplayName("Should throw TooManyAttemptsException when max attempts exceeded")
    void shouldThrowTooManyAttemptsExceptionWhenMaxAttemptsExceeded() {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        OtpHistory history = new OtpHistory(email, "encrypted_123456");
        history.incrementAttempts();
        history.incrementAttempts();
        history.incrementAttempts();
        history.incrementAttempts();
        history.incrementAttempts();
        
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(history));
        Mockito.when(mockValidationConfig.getMaxAttempts()).thenReturn(5);

        // When/Then
        assertThrows(TooManyAttemptsException.class, () -> {
            validatorService.validateOtp(email, otp);
        });
    }

    @Test
    @DisplayName("Should not validate expired OTP after new one is generated")
    void shouldNotValidateExpiredOtpAfterNewOneGenerated() {
        // Given
        String email = "test2@example.com";
        String oldOtp = "111111";
        String newOtp = "222222";
        OtpHistory oldHistory = new OtpHistory(email, "encrypted_" + oldOtp);
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.of(oldHistory));
        Mockito.when(mockHistoryRepository.save(any(OtpHistory.class)))
            .thenAnswer(i -> i.getArgument(0));

        // Quando um novo OTP é gerado, o anterior é expirado
        OtpHistory newHistory = new OtpHistory(email, "encrypted_" + newOtp);
        validatorService.storeOtp(email, newHistory);
        assertEquals(OtpStatus.EXPIRED, oldHistory.getStatus());

        // Agora, tentar validar o OTP antigo deve lançar ResourceNotFoundException
        Mockito.when(mockHistoryRepository.findByEmailAndStatus(email, OtpStatus.ACTIVE))
            .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            validatorService.validateOtp(email, oldOtp);
        });
    }
} 