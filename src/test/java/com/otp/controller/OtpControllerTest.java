package com.otp.controller;

import com.otp.exception.GlobalExceptionHandler;
import com.otp.model.OtpResponse;
import com.otp.service.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(OtpController.class)
@Import(GlobalExceptionHandler.class)
public class OtpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OtpService otpService;

    @Test
    void shouldGenerateOtpSuccessfully() throws Exception {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.generateOtp(email)).thenReturn(new OtpResponse(otp, true));

        // When/Then
        mockMvc.perform(post("/api/otp/generate")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.otp").value(otp))
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));

        verify(otpService).generateOtp(email);
    }

    @Test
    void shouldValidateOtpSuccessfully() throws Exception {
        // Given
        String email = "test@example.com";
        String otp = "123456";
        when(otpService.validateOtp(email, otp)).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/otp/validate")
                        .param("email", email)
                        .param("otp", otp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP validated successfully"));

        verify(otpService).validateOtp(email, otp);
    }

    @Test
    void shouldRevokeOtpSuccessfully() throws Exception {
        // Given
        String email = "test@example.com";
        String reason = "Security concern";
        when(otpService.revokeOtp(email, reason)).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/api/otp/revoke")
                        .param("email", email)
                        .param("reason", reason))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP revoked successfully"));

        verify(otpService).revokeOtp(email, reason);
    }

    @Test
    void shouldRejectInvalidEmailFormat() throws Exception {
        // Given
        String invalidEmail = "invalid-email";
        
        // Mock the service to avoid NullPointerException
        when(otpService.generateOtp(anyString())).thenReturn(new OtpResponse("123456", true));
        
        // When/Then
        mockMvc.perform(post("/api/otp/generate")
                        .param("email", invalidEmail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    void shouldRejectEmptyOtp() throws Exception {
        // Given
        String email = "test@example.com";
        String emptyOtp = "";
        
        // When/Then
        mockMvc.perform(post("/api/otp/validate")
                        .param("email", email)
                        .param("otp", emptyOtp))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }
} 