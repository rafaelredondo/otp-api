package com.otp.controller;

import com.otp.config.TestOtpConfig;
import com.otp.exception.OtpValidationException;
import com.otp.exception.ResourceNotFoundException;
import com.otp.exception.TooManyAttemptsException;
import com.otp.service.OtpService;
import com.otp.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OtpControllerExceptionTest {

    private MockMvc mockMvc;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private OtpController otpController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(otpController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void handleOtpValidationException() throws Exception {
        // Given
        when(otpService.validateOtp(anyString(), anyString()))
                .thenThrow(new OtpValidationException("Invalid OTP"));

        // When/Then
        mockMvc.perform(post("/api/otp/validate")
                        .param("email", "test@example.com")
                        .param("otp", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Invalid OTP"));
    }

    @Test
    void handleResourceNotFoundException() throws Exception {
        // Given
        when(otpService.validateOtp(anyString(), anyString()))
                .thenThrow(new ResourceNotFoundException("No active OTP found"));

        // When/Then
        mockMvc.perform(post("/api/otp/validate")
                        .param("email", "test@example.com")
                        .param("otp", "123456"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("No active OTP found"));
    }

    @Test
    void handleTooManyAttemptsException() throws Exception {
        // Given
        when(otpService.validateOtp(anyString(), anyString()))
                .thenThrow(new TooManyAttemptsException("Too many attempts"));

        // When/Then
        mockMvc.perform(post("/api/otp/validate")
                        .param("email", "test@example.com")
                        .param("otp", "123456"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.error").value("Too Many Attempts"))
                .andExpect(jsonPath("$.message").value("Too many attempts"));
    }
} 