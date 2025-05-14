package com.otp.exception;

public class OtpRevocationException extends RuntimeException {
    public OtpRevocationException(String message) {
        super(message);
    }

    public OtpRevocationException(String message, Throwable cause) {
        super(message, cause);
    }
} 