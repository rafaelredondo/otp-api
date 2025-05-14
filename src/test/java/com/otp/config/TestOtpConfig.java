package com.otp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestOtpConfig {

    @Bean
    public DefaultOtpConfig otpConfig() {
        DefaultOtpConfig config = new DefaultOtpConfig();
        
        // Set validation properties
        DefaultOtpConfig.DefaultValidation validation = new DefaultOtpConfig.DefaultValidation();
        validation.setMaxAttempts(5);
        validation.setWindowMinutes(15);
        validation.setExpirationMinutes(30);
        config.setValidation(validation);
        
        // Set generation properties
        DefaultOtpConfig.DefaultGeneration generation = new DefaultOtpConfig.DefaultGeneration();
        generation.setLength(6);
        generation.setPrefix("");
        config.setGeneration(generation);
        
        return config;
    }
} 