package com.otp.service;

import java.security.SecureRandom;
import java.util.Base64;

public interface EncryptionService {
    String encrypt(String value);
    String decrypt(String encrypted);
    default String generateNewKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}