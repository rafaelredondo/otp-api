package com.otp.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;

@Service
public class DefaultEncryptionService implements EncryptionService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultEncryptionService.class);
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] FIXED_IV = new byte[16]; // IV fixo para garantir que o mesmo valor gere o mesmo resultado
    
    @Value("${encryption.key:}")
    private String secretKey;

    @PostConstruct
    public void init() {
        logger.debug("Initializing DefaultEncryptionService");
        // Não valida a chave na inicialização, só quando for usar
        // A chave será validada antes de cada operação de encriptação/decriptação
    }

    /**
     * Valida a chave de encriptação.
     * 
     * @throws IllegalStateException se a chave de encriptação não estiver configurada ou 
     *         não tiver o formato Base64 válido
     */
    private void validateKey() {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            logger.error("Encryption key is not configured");
            throw new IllegalStateException("Encryption key is not configured");
        }
        
        logger.debug("Encryption key loaded successfully, length: {}", secretKey.length());
        
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            
            // Verifica se a chave tem o tamanho esperado para AES-256
            if (keyBytes.length != 32) {
                logger.error("Invalid key length: {}. Expected 32 bytes for AES-256", keyBytes.length);
                throw new IllegalStateException("Invalid key length. Expected 32 bytes for AES-256");
            }
            
            logger.debug("Key decoded successfully, length: {} bytes", keyBytes.length);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 key", e);
            throw new IllegalStateException("Invalid Base64 key", e);
        }
    }

    @Override
    public String encrypt(String value) {
        try {
            logger.debug("Starting encryption with key length: {}", secretKey != null ? secretKey.length() : "null");
            
            if (value == null) {
                logger.error("Value to encrypt is null");
                throw new IllegalArgumentException("Value to encrypt cannot be null");
            }
            
            validateKey();

            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(FIXED_IV);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            
            byte[] encryptedBytes = cipher.doFinal(value.getBytes("UTF-8"));
            String encryptedValue = Base64.getEncoder().encodeToString(encryptedBytes);
            
            logger.debug("Encryption completed successfully");
            return encryptedValue;
        } catch (Exception e) {
            logger.error("Error during encryption", e);
            throw new RuntimeException("Error during encryption", e);
        }
    }

    @Override
    public String decrypt(String encryptedValue) {
        try {
            if (encryptedValue == null) {
                logger.error("Value to decrypt is null");
                throw new IllegalArgumentException("Value to decrypt cannot be null");
            }
            
            validateKey();
            
            byte[] keyBytes = Base64.getDecoder().decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(FIXED_IV);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            
            byte[] decodedEncryptedBytes = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedEncryptedBytes);
            
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            logger.error("Error during decryption", e);
            throw new RuntimeException("Error during decryption", e);
        }
    }

    @Override
    public String generateValidKey() {
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            byte[] key = new byte[32]; // 256 bits para AES-256
            random.nextBytes(key);
            return Base64.getEncoder().encodeToString(key);
        } catch (Exception e) {
            logger.error("Error generating encryption key", e);
            throw new RuntimeException("Error generating encryption key", e);
        }
    }
} 