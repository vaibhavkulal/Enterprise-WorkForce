package com.enterprise.workforce.controller;

import com.enterprise.workforce.dto.AuthRequest;
import com.enterprise.workforce.dto.AuthResponse;
import com.enterprise.workforce.dto.ChangePasswordRequest;
import com.enterprise.workforce.dto.ResetPasswordRequest;
import com.enterprise.workforce.exception.BadRequestException;
import com.enterprise.workforce.repository.UserRepository;
import com.enterprise.workforce.service.AuthService;
import com.enterprise.workforce.service.impl.RefreshTokenService;
import com.enterprise.workforce.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3333" )
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user login and token refresh")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;


    @Operation(summary = "Authenticate user and generate JWT tokens", description = "Validates username and password, then issues access and refresh tokens for session management.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))), @ApiResponse(responseCode = "401", description = "Invalid credentials"), @ApiResponse(responseCode = "400", description = "Bad request or missing fields")})
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String loginResponse = authService.login(request.getUsername(), request.getPassword());

        // Split response "accessToken::refreshToken"
        String[] tokens = loginResponse.split("::");
        if (tokens.length < 2) {
            throw new RuntimeException("Invalid token response from AuthService");
        }

        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
    }

    @Operation(summary = "Refresh access token", description = "Generates a new access token using a valid refresh token without re-authenticating the user.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Access token refreshed successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))), @ApiResponse(responseCode = "400", description = "Invalid or missing refresh token"), @ApiResponse(responseCode = "401", description = "Refresh token expired or unauthorized")})
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is missing or invalid");
        }
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }


        String newAccessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> body, Principal principal) {
        // client can send refreshToken; also revoke all for user
        String refreshToken = body.get("refreshToken");
        if (refreshToken != null) refreshTokenService.revoke(refreshToken);
        if (principal != null) refreshTokenService.revokeAllForUser(principal.getName());
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }


//    @Operation(summary = "Logout user (invalidate access and refresh tokens)", description = "Immediately invalidates both access and refresh tokens to prevent further access.")
//    @ApiResponses({@ApiResponse(responseCode = "200", description = "Logout successful — tokens invalidated"), @ApiResponse(responseCode = "400", description = "Missing or invalid tokens")})
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
//        }
//
//        String token = authHeader.substring(7);
//        jwtUtil.invalidateToken(token);
//
//        return ResponseEntity.ok("Logout successful. Token invalidated.");
//    }

    @Operation(summary = "Change user password", description = "Allows logged-in user to change their password.")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

//    @Operation(summary = "Reset forgotten password", description = "Allows user to reset password if forgotten (admin or OTP flow).")
//    @PostMapping("/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
//        authService.resetPassword(request);
//        return ResponseEntity.ok("Password reset successfully");
//    }

}
