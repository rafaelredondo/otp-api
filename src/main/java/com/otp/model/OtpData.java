package com.otp.model;

import java.time.Instant;

public record OtpData(String otp, Instant timestamp) {} 