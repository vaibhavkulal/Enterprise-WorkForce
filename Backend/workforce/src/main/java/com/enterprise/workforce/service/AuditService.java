package com.enterprise.workforce.service;

import com.enterprise.workforce.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AuditService {
    void saveLog(String username, String action, String entityName, Long entityId, String details);
    Page<AuditLog> getAllLogs(Pageable pageable);
    List<AuditLog> getLogsByUser(String username);
}
