package com.otp.repository;

import com.otp.model.OtpAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;
import java.util.List;

public interface OtpAttemptRepository extends JpaRepository<OtpAttempt, Long> {
    @Query("SELECT a FROM OtpAttempt a WHERE a.email = ?1 AND a.attemptTime > ?2")
    List<OtpAttempt> findRecentAttempts(String email, Instant since);
} 