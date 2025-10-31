package com.enterprise.workforce.aop;

import com.enterprise.workforce.annotations.Auditable;
import com.enterprise.workforce.service.impl.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final HttpServletRequest request;

    /**
     * Logs an audit entry whenever a method annotated with @Auditable executes successfully.
     */
    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void logAction(JoinPoint joinPoint, Object result, Auditable auditable) {
        // Extract user info
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = Optional.ofNullable(auth).map(Authentication::getName).orElse("SYSTEM");
        String role = Optional.ofNullable(auth).map(a -> a.getAuthorities().toString()).orElse("UNKNOWN");

        // Extract audit metadata
        String action = auditable.action().isEmpty() ? joinPoint.getSignature().getName() : auditable.action();
        String entity = auditable.entity().isEmpty() ? joinPoint.getTarget().getClass().getSimpleName() : auditable.entity();

        // Capture IP
        String ipAddress = Optional.ofNullable(request.getHeader("X-FORWARDED-FOR")).orElse(request.getRemoteAddr());

        // Try to extract entity ID from the result (if it's an entity with getId)
        Long entityId = extractEntityId(result);

        // Save the audit entry
        auditLogService.saveLog(username, action, entity, entityId, "Executed " + action + " on " + entity + " at " + LocalDateTime.now(), role, ipAddress);

        System.out.println("✅ [AUDIT] " + username + " performed " + action + " on " + entity);
    }

    /**
     * Helper to extract entity ID (if result has getId()).
     */
    private Long extractEntityId(Object result) {
        try {
            if (result == null) return null;
            var method = result.getClass().getMethod("getId");
            Object idValue = method.invoke(result);
            return (idValue instanceof Long) ? (Long) idValue : null;
        } catch (Exception e) {
            return null;
        }
    }
}
