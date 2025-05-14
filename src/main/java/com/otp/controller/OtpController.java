package com.otp.controller;

import com.otp.model.ApiResponse;
import com.otp.model.OtpResponse;
import com.otp.service.OtpService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@Validated
@CrossOrigin("*")
public class OtpController {
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse> generateOtp(
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email) {
        OtpResponse response = otpService.generateOtp(email);
        return ResponseEntity.ok(new ApiResponse(response.otp(), "OTP sent successfully"));
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateOtp(
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email,
            @RequestParam 
            @NotBlank(message = "OTP cannot be empty")
            String otp) {
        boolean valid = otpService.validateOtp(email, otp);
        if (!valid) {
            throw new com.otp.exception.OtpValidationException("OTP inválido para o email informado");
        }
        return ResponseEntity.ok(new ApiResponse(null, "OTP validated successfully"));
    }

    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse> revokeOtp(
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email,
            @RequestParam String reason) {
        otpService.revokeOtp(email, reason);
        return ResponseEntity.ok(new ApiResponse(null, "OTP revoked successfully"));
    }
} 