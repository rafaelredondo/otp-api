package com.otp.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public class OtpRequest {
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    private String email;
    
    @Pattern(regexp = "^[0-9]{6}$", message = "OTP must be 6 digits")
    private String otp;

    public OtpRequest() {
        // Construtor sem argumentos necessário para deserialização JSON
    }

    public OtpRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
} 