package com.enterprise.workforce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String action;
    private String entity;
    private Long entityId;
    private String details;
    private LocalDateTime timestamp;
    private String role;
    private String ipAddress;

}
