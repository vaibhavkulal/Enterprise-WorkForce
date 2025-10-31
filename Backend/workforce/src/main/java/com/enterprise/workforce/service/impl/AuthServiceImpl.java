package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.dto.ChangePasswordRequest;
import com.enterprise.workforce.dto.ResetPasswordRequest;
import com.enterprise.workforce.entity.User;
import com.enterprise.workforce.repository.RoleRepository;
import com.enterprise.workforce.repository.UserRepository;
import com.enterprise.workforce.service.AuthService;
import com.enterprise.workforce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    // Temporary in-memory storage for blacklisted tokens (use Redis in production)
    private final Set<String> blacklistedTokens = new HashSet<>();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    @Override
    public String login(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        //  Generate access and refresh tokens
        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getRole().getName().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getRole().getName().name());

        return accessToken + "::" + refreshToken;
    }

    @Override
    public String refreshToken(String refreshToken) {
        if (blacklistedTokens.contains(refreshToken)) {
            throw new RuntimeException("Token has been invalidated. Please login again.");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        org.springframework.security.core.userdetails.UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).roles(user.getRole().getName().name()).build();

        if (!jwtUtil.validateToken(refreshToken, userDetails)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole().getName().name());
    }


    @Override
    public void logout(String accessToken, String refreshToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new RuntimeException("Access token missing");
        }
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token missing");
        }

        //  Blacklist tokens in both AuthServiceImpl and JwtUtil
        blacklistedTokens.add(accessToken);
        blacklistedTokens.add(refreshToken);

        jwtUtil.invalidateToken(accessToken);
        jwtUtil.invalidateToken(refreshToken);
    }


    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        //  Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        //  Encode and save new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        // In real app, validate OTP/email link before allowing reset.
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

}
