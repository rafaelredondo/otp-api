package com.otp.service.validator;

import java.time.Instant;

public record ValidationContext(
    String email,
    String providedOtp,
    String storedOtp,
    Instant timestamp
) {} 