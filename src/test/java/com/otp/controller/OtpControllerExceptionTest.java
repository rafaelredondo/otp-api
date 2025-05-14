package com.otp.controller;

import com.otp.exception.OtpValidationException;
import com.otp.exception.ResourceNotFoundException;
import com.otp.exception.TooManyAttemptsException;
import com.otp.model.OtpResponse;
import com.otp.service.OtpService;
import com.otp.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OtpController.class)
@Import(GlobalExceptionHandler.class)
public class OtpControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OtpService otpService;

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
    
    @Test
    void handleInvalidEmailFormat() throws Exception {
        // Given
        String invalidEmail = "invalid-email";
        
        // Mock service methods to avoid NullPointerException
        when(otpService.generateOtp(anyString())).thenReturn(new OtpResponse("123456", true));
        when(otpService.validateOtp(anyString(), anyString())).thenReturn(true);
        when(otpService.revokeOtp(anyString(), anyString())).thenReturn(true);
        
        // For all endpoints that require an email
        // Test generate
        mockMvc.perform(post("/api/otp/generate")
                        .param("email", invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
                
        // Test validate
        mockMvc.perform(post("/api/otp/validate")
                        .param("email", invalidEmail)
                        .param("otp", "123456"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
                
        // Test revoke
        mockMvc.perform(post("/api/otp/revoke")
                        .param("email", invalidEmail)
                        .param("reason", "testing"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
} 