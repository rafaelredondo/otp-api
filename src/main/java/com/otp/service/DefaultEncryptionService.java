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
    
    @Value("${encryption.key.secret}")
    private String secretKey;

    /**
     * Valida a chave de criptografia.
     * @param throwException Se deve lançar exceção em caso de chave inválida
     * @return true se a chave for válida, false caso contrário
     */
    private boolean validateKey(boolean throwException) {
        if (secretKey == null || secretKey.trim().isEmpty()) {
            logger.error("Encryption key is not configured");
            if (throwException) {
                throw new IllegalStateException("Encryption key is not configured");
            }
            return false;
        }
        
        try {
            byte[] keyBytes = Base64.getDecoder().decode(secretKey.trim());
            logger.debug("Key decoded successfully, length: {} bytes", keyBytes.length);
            
            if (keyBytes.length != 32) {
                logger.error("Invalid key length: {} bytes (expected 32 bytes)", keyBytes.length);
                if (throwException) {
                    throw new IllegalArgumentException("Key must be exactly 32 bytes (256 bits) when decoded");
                }
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 key format: {}", e.getMessage());
            if (throwException) {
                throw new IllegalStateException("Invalid encryption key format", e);
            }
            return false;
        }
    }

    @PostConstruct
    public void init() {
        logger.debug("Initializing DefaultEncryptionService");
        if (secretKey == null) {
            logger.error("Encryption key is null after initialization");
            throw new IllegalStateException("Encryption key is not configured");
        }
        logger.debug("Encryption key loaded successfully, length: {}", secretKey.length());
        
        try {
            if (!validateKey(false)) {
                String newKey = generateNewKey();
                logger.info("Generated new valid key: {}", newKey);
                throw new IllegalStateException("Invalid encryption key. Please use this key: " + newKey);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 key format: {}", e.getMessage());
            throw new IllegalStateException("Invalid encryption key format", e);
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
            
            // Valida a chave antes de prosseguir
            validateKey(true);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey.trim()), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(FIXED_IV);
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            
            String result = Base64.getEncoder().encodeToString(encrypted);
            logger.debug("Encryption completed successfully");
            return result;
        } catch (Exception e) {
            logger.error("Error during encryption: {}", e.getMessage(), e);
            throw new RuntimeException("Error encrypting value: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateNewKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
} 