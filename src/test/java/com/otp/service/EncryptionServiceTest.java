package com.otp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private static final String TEST_KEY = "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="; // Chave de teste

    @BeforeEach
    void setUp() {
        encryptionService = new DefaultEncryptionService();
        ReflectionTestUtils.setField(encryptionService, "secretKey", TEST_KEY);
    }

    @Test
    @DisplayName("Deve gerar encriptações iguais para o mesmo OTP")
    void shouldGenerateEqualEncryptionsForSameOtp() {
        // Given
        String otp = "123456";

        // When
        String firstEncryption = encryptionService.encrypt(otp);
        String secondEncryption = encryptionService.encrypt(otp);

        // Then
        assertEquals(firstEncryption, secondEncryption, 
            "Encriptações do mesmo OTP devem ser iguais devido ao IV fixo");
    }

    @Test
    @DisplayName("Deve gerar chave de encriptação válida")
    void shouldGenerateValidEncryptionKey() {
        // When
        String key = encryptionService.generateNewKey();

        // Then
        assertNotNull(key, "Chave gerada não deve ser nula");
        assertTrue(key.length() > 0, "Chave gerada não deve ser vazia");
    }
} 