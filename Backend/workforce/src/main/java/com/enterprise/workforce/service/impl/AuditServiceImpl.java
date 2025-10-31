package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.AuditLog;
import com.enterprise.workforce.repository.AuditLogRepository;
import com.enterprise.workforce.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void saveLog(String username, String action, String entityName, Long entityId, String details) {
        AuditLog log = AuditLog.builder()
                .username(username)
                .action(action)
                .entity(entityName)
                .entityId(entityId)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
    }

    @Override
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public List<AuditLog> getLogsByUser(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }
}
