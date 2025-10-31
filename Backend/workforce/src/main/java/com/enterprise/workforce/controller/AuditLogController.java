package com.enterprise.workforce.controller;

import com.enterprise.workforce.entity.AuditLog;
import com.enterprise.workforce.service.impl.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit")  // changed base path
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

//    @GetMapping
//    public ResponseEntity<Page<AuditLog>> getAllLogs(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
//        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
//    }

    @GetMapping("/{userId}")
    public ResponseEntity<Page<AuditLog>> getUserLogs(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        return ResponseEntity.ok(auditLogService.getLogsByUser(userId, pageable));
    }
//    @GetMapping
//    public ResponseEntity<Page<AuditLog>> getAllLogs(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size) {
//
//        // Ensure page is not negative
//        int pageIndex = Math.max(page - 1, 0);
//        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("timestamp").descending());
//        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
//    }

    @GetMapping
    public ResponseEntity<Page<AuditLog>> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("📢 Fetching audit logs for page " + page);

        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> logs = auditLogService.getAllLogs(pageable);

        System.out.println("Logs found: " + logs.getTotalElements());
        return ResponseEntity.ok(logs);
    }


}
