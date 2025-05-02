package com.otp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otp_attempts")
public class OtpAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private Instant attemptTime;
    
    public OtpAttempt() {}
    
    public OtpAttempt(String email, Instant attemptTime) {
        this.email = email;
        this.attemptTime = attemptTime;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public Instant getAttemptTime() { return attemptTime; }
} 