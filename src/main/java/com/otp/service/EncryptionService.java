package com.otp.service;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Interface para serviços de encriptação/decriptação
 */
public interface EncryptionService {
    
    /**
     * Encripta um valor fornecido
     * @param value valor a ser encriptado
     * @return valor encriptado em formato Base64
     */
    String encrypt(String value);
    
    /**
     * Decripta um valor encriptado
     * @param encryptedValue valor encriptado em formato Base64
     * @return valor original decriptado
     */
    String decrypt(String encryptedValue);
    
    /**
     * Gera uma nova chave de encriptação válida
     * @return chave de encriptação em formato Base64
     */
    String generateValidKey();
    
    /**
     * Gera uma nova chave de encriptação (mantido para compatibilidade)
     * @return chave de encriptação em formato Base64
     * @deprecated Use o método {@link #generateValidKey()} em seu lugar
     */
    @Deprecated
    default String generateNewKey() {
        return generateValidKey();
    }
}