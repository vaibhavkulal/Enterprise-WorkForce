package com.enterprise.workforce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Username or email is required")
    private String username; // <-- Add this field

    @NotBlank(message = "OTP is required")
    private String otp; // Optional if you're doing OTP verification

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String newPassword;
}
