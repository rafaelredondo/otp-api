package com.otp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Interface for OTP configuration
 */
public interface OtpConfig {
    Validation getValidation();
    Generation getGeneration();
    
    interface Validation {
        int getMaxAttempts();
        int getWindowMinutes();
        int getExpirationMinutes();
    }
    
    interface Generation {
        int getLength();
        String getPrefix();
    }
}

/**
 * Default implementation of OtpConfig
 */
@Configuration
@ConfigurationProperties(prefix = "otp")
class DefaultOtpConfig implements OtpConfig {

    private final DefaultValidation validation = new DefaultValidation();
    private final DefaultGeneration generation = new DefaultGeneration();

    @Override
    public Validation getValidation() {
        return validation;
    }
    
    public void setValidation(DefaultValidation validation) {
        // Used for property binding
    }

    @Override
    public Generation getGeneration() {
        return generation;
    }
    
    public void setGeneration(DefaultGeneration generation) {
        // Used for property binding
    }

    public static class DefaultValidation implements Validation {
        private int maxAttempts = 5;
        private int windowMinutes = 15;
        private int expirationMinutes = 30;

        @Override
        public int getMaxAttempts() {
            return maxAttempts;
        }

        public void setMaxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        @Override
        public int getWindowMinutes() {
            return windowMinutes;
        }

        public void setWindowMinutes(int windowMinutes) {
            this.windowMinutes = windowMinutes;
        }

        @Override
        public int getExpirationMinutes() {
            return expirationMinutes;
        }

        public void setExpirationMinutes(int expirationMinutes) {
            this.expirationMinutes = expirationMinutes;
        }
    }

    public static class DefaultGeneration implements Generation {
        private int length = 6;
        private String prefix = "";

        @Override
        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        @Override
        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
} 