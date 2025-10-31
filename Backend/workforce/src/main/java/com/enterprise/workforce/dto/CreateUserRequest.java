package com.enterprise.workforce.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String email;
    private String password;
    private String role;     // e.g. "HR"
    private String status;   // e.g. "ACTIVE"
}
