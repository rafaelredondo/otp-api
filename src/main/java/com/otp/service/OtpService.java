package com.otp.service;

import com.otp.model.OtpResponse;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.otp.model.OtpHistory;

@Service
public class OtpService {
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private final OtpGeneratorService generatorService;
    private final OtpValidatorService validatorService;
    private final EncryptionService encryptionService;

    public OtpService(OtpGeneratorService generatorService, OtpValidatorService validatorService, EncryptionService encryptionService) {
        this.generatorService = generatorService;
        this.validatorService = validatorService;
        this.encryptionService = encryptionService;
    }

    public OtpResponse generateOtp(@Email String email) {
        logger.debug("Generating OTP for email: {}", email);
        try {
            OtpResponse response = generatorService.generateOtp(email);
            if (response.delivered()) {
                String encryptedOtp = encryptionService.encrypt(response.otp());
                OtpHistory history = new OtpHistory(email.toLowerCase(), encryptedOtp);
                validatorService.storeOtp(email, history);
                logger.info("OTP generated and stored successfully for email: {}", email);
            } else {
                logger.error("Failed to deliver OTP for email: {}", email);
            }
            return response;
        } catch (Exception e) {
            logger.error("Error generating OTP for email: {}", email, e);
            throw e;
        }
    }

    public boolean validateOtp(@Email String email, String otp) {
        logger.debug("Validating OTP for email: {}", email);
        try {
            boolean isValid = validatorService.validateOtp(email, otp);
            if (isValid) {
                logger.info("OTP validated successfully for email: {}", email);
            } else {
                logger.warn("Invalid OTP attempt for email: {}", email);
            }
            return isValid;
        } catch (Exception e) {
            logger.error("Error validating OTP for email: {}", email, e);
            throw e;
        }
    }

    public boolean revokeOtp(@Email String email, String reason) {
        logger.debug("Revoking OTP for email: {} with reason: {}", email, reason);
        try {
            boolean revoked = validatorService.revokeOtp(email.toLowerCase(), reason);
            if (revoked) {
                logger.info("OTP revoked successfully for email: {}", email);
            } else {
                logger.warn("Failed to revoke OTP for email: {} - no active OTP found", email);
            }
            return revoked;
        } catch (Exception e) {
            logger.error("Error revoking OTP for email: {}", email, e);
            throw e;
        }
    }
} 