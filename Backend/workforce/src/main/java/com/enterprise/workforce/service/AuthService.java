package com.enterprise.workforce.service;

import com.enterprise.workforce.dto.ChangePasswordRequest;
import com.enterprise.workforce.dto.ResetPasswordRequest;
import com.enterprise.workforce.entity.User;

import java.util.Optional;

public interface AuthService {

    User register(User user);

    String login(String username, String password);

    String refreshToken(String refreshToken);

    void logout(String accessToken, String refreshToken);

    void changePassword(ChangePasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    Optional<User> findByUsername(String username);
}
