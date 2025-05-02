package com.otp.model;

public record OtpResponse(
    String otp,
    boolean delivered
) {} 