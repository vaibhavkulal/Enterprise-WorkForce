package com.enterprise.workforce.dto;

import lombok.Data;

/**
 * DTO for change password flow.
 * Fields must match accessor calls in the service:
 * - getUsername()
 * - getCurrentPassword()
 * - getNewPassword()
 */
@Data
public class ChangePasswordRequest {
    private String username;
    private String currentPassword; // used by AuthServiceImpl#getCurrentPassword()
    private String newPassword;     // used by AuthServiceImpl#getNewPassword()
}
