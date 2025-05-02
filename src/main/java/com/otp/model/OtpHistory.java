package com.otp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otp_history")
public class OtpHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String encryptedOtp;
    private Instant createdAt;
    private Instant updatedAt;
    
    @Enumerated(EnumType.STRING)
    private OtpStatus status;
    
    private String revokedReason;
    private Instant revokedAt;
    private Instant usedAt;
    private Integer attemptCount = 0;

    public OtpHistory() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.status = OtpStatus.ACTIVE;
        this.attemptCount = 0;
    }

    public OtpHistory(String email, String encryptedOtp) {
        this();
        this.email = email;
        this.encryptedOtp = encryptedOtp;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getEncryptedOtp() { return encryptedOtp; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public OtpStatus getStatus() { return status; }
    public String getRevokedReason() { return revokedReason; }
    public Instant getRevokedAt() { return revokedAt; }
    public Instant getUsedAt() { return usedAt; }
    public Integer getAttemptCount() { return attemptCount; }

    public void setStatus(OtpStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void incrementAttempts() {
        this.attemptCount++;
        this.updatedAt = Instant.now();
    }

    public void revoke(String reason) {
        this.status = OtpStatus.REVOKED;
        this.revokedReason = reason;
        this.revokedAt = Instant.now();
        this.updatedAt = this.revokedAt;
    }

    public void markAsUsed() {
        this.status = OtpStatus.USED;
        this.usedAt = Instant.now();
        this.updatedAt = this.usedAt;
    }

    public void markAsExpired() {
        this.status = OtpStatus.EXPIRED;
        this.updatedAt = Instant.now();
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setEncryptedOtp(String encryptedOtp) {
        this.encryptedOtp = encryptedOtp;
    }
} 