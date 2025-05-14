package com.otp.exception;

public class TooManyAttemptsException extends OtpException {
    public TooManyAttemptsException(String message) {
        super(message);
    }
} 