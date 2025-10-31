package com.enterprise.workforce.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private LocalDateTime appStartTime;

    @Value("${spring.application.name:Enterprise Workforce & Access Management System}")
    private String appName;

    @Value("${spring.profiles.active:DEV}")
    private String activeProfile;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.datasource.url:Not Configured}")
    private String datasourceUrl;

    @PostConstruct
    public void init() {
        appStartTime = LocalDateTime.now();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new LinkedHashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("environment", activeProfile);
        status.put("uptime", getUptime());
        status.put("server", getHostName());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> version() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("application", appName);
        info.put("version", "1.0.0");
        info.put("environment", activeProfile);
        info.put("serverPort", serverPort);
        info.put("database", datasourceUrl);
        info.put("startedAt", appStartTime.toString());
        info.put("timezone", ZoneId.systemDefault().toString());
        info.put("author", "Vaibhav Kulal");
        info.put("license", "MIT");
        return ResponseEntity.ok(info);
    }

    private String getUptime() {
        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        long uptimeMs = runtimeBean.getUptime();
        long seconds = (uptimeMs / 1000) % 60;
        long minutes = (uptimeMs / (1000 * 60)) % 60;
        long hours = (uptimeMs / (1000 * 60 * 60)) % 24;
        long days = uptimeMs / (1000 * 60 * 60 * 24);
        return String.format("%d days, %d hours, %d minutes, %d seconds", days, hours, minutes, seconds);
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
