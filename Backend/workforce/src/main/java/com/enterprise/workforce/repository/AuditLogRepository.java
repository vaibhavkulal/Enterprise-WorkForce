package com.enterprise.workforce.repository;

import com.enterprise.workforce.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    Page<AuditLog> findAllByUsername(String username, Pageable pageable);
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
}
