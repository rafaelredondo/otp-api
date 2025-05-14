package com.otp.service;

import com.otp.exception.OtpGenerationException;
import com.otp.exception.OtpValidationException;
import com.otp.exception.OtpRevocationException;
import com.otp.model.OtpResponse;
import org.springframework.stereotype.Service;
import jakarta.validation.constraints.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.otp.model.OtpHistory;

public interface OtpService {
    OtpResponse generateOtp(@Email String email);
    boolean validateOtp(@Email String email, String otp);
    boolean revokeOtp(@Email String email, String reason);
}

@Service
class DefaultOtpService implements OtpService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultOtpService.class);
    private final OtpGeneratorService generatorService;
    private final OtpValidatorService validatorService;
    private final EncryptionService encryptionService;

    public DefaultOtpService(OtpGeneratorService generatorService, 
                           OtpValidatorService validatorService, 
                           EncryptionService encryptionService) {
        this.generatorService = generatorService;
        this.validatorService = validatorService;
        this.encryptionService = encryptionService;
    }

    @Override
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
                throw new OtpGenerationException("Failed to deliver OTP to " + email);
            }
            return response;
        } catch (Exception e) {
            logger.error("Error generating OTP for email: {}", email, e);
            if (e instanceof OtpGenerationException) {
                throw e;
            }
            throw new OtpGenerationException("Error generating OTP for email: " + email, e);
        }
    }

    @Override
    public boolean validateOtp(@Email String email, String otp) {
        logger.debug("Validating OTP for email: {}", email);
        try {
            return validatorService.validateOtp(email.toLowerCase(), otp);
        } catch (Exception e) {
            logger.error("Error validating OTP for email: {}", email, e);
            throw new OtpValidationException("Error validating OTP for email: " + email, e);
        }
    }

    @Override
    public boolean revokeOtp(@Email String email, String reason) {
        logger.debug("Revoking OTP for email: {} with reason: {}", email, reason);
        try {
            return validatorService.revokeOtp(email.toLowerCase(), reason);
        } catch (Exception e) {
            logger.error("Error revoking OTP for email: {}", email, e);
            throw new OtpRevocationException("Error revoking OTP for email: " + email, e);
        }
    }
} 