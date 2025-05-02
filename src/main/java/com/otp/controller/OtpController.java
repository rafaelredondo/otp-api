package com.otp.controller;

import com.otp.model.ApiResponse;
import com.otp.model.OtpResponse;
import com.otp.service.OtpService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@Validated
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
        if (response.delivered()) {
            return ResponseEntity.ok(new ApiResponse(response.otp(), "OTP sent successfully"));
        } else {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse(null, "Failed to send OTP. Please try again later."));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateOtp(
            @RequestParam 
            @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
            String email,
            @RequestParam 
            @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
            String otp) {
        boolean isValid = otpService.validateOtp(email, otp);
        if (isValid) {
            return ResponseEntity.ok(new ApiResponse(null, "OTP validated successfully"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(null, "Invalid OTP"));
        }
    }
} 