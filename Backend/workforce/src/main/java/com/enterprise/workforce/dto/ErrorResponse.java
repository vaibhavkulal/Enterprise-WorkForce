package com.enterprise.workforce.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
