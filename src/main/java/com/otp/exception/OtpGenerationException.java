package com.otp.exception;

public class OtpGenerationException extends OtpException {
    public OtpGenerationException(String message) {
        super(message);
    }
    
    public OtpGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
} 