package com.otp.repository;

import com.otp.model.OtpHistory;
import com.otp.model.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.time.Instant;
import java.util.List;

public interface OtpHistoryRepository extends JpaRepository<OtpHistory, Long> {
    Optional<OtpHistory> findByEmailAndStatus(String email, OtpStatus status);
    List<OtpHistory> findByEmailAndCreatedAtAfter(String email, Instant since);
    Optional<OtpHistory> findByEmailAndOtpAndStatus(String email, String otp, OtpStatus status);
} 