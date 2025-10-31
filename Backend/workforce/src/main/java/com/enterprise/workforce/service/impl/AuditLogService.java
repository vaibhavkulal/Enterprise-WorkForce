package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.AuditLog;
import com.enterprise.workforce.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public void saveLog(String username, String action, String entity, Long entityId, String details, String role, String ip) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .role(role)
                .ipAddress(ip)
                .build();

        auditLogRepository.save(log);
    }

    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    public Page<AuditLog> getLogsByUser(String username, Pageable pageable) {
        return auditLogRepository.findAllByUsername(username, pageable);
    }

    public List<AuditLog> getLogsByUserSimple(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }
}
