package com.otp.model;

public record OtpNotificationMessage(
    String email,
    String otp,
    int retryCount
) {} 