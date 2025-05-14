package com.otp.service;

import java.security.SecureRandom;
import java.util.Base64;

public interface EncryptionService {
    /**
     * Encripta um valor usando um IV fixo para garantir que o mesmo valor
     * sempre gere o mesmo resultado encriptado.
     */
    String encrypt(String value);

    /**
     * Gera uma nova chave de encriptação válida.
     */
    default String generateNewKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}