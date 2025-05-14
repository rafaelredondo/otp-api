package com.otp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class Main {
    public static void main(String[] args) {
        // Set test profile if not already set
        if (System.getProperty("spring.profiles.active") == null) {
            System.setProperty("spring.profiles.active", "test");
        }
        SpringApplication.run(Main.class, args);
    }
} 