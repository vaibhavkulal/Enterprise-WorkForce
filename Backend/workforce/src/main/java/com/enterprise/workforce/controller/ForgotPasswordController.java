package com.enterprise.workforce.controller;

import com.enterprise.workforce.dto.ForgotPasswordRequest;
import com.enterprise.workforce.dto.ResetPasswordOtpRequest;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.repository.UserRepository;
import com.enterprise.workforce.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final AuthServiceImpl authService;

    /**
     * Step 1: User enters email → OTP generated and displayed (Dev mode)
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User with this email does not exist."));
        }

        String otp = RandomString.make(6);
        user.setOtp(otp);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "OTP generated successfully (DEV MODE)", "otp", otp));
    }

    /**
     * Step 2: User submits OTP + new password → validate and reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordOtpRequest request) {
        User user = userRepository.findByOtp(request.getOtp()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid or expired OTP"));
        }

        authService.updatePassword(user.getEmail(), request.getNewPassword());
        user.setOtp(null);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }
}
