package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    // Store blacklisted tokens and their expiry time (in milliseconds)
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    // Default expiry: 1 hour (3600000 ms) – match your JWT expiry
    private static final long EXPIRY_DURATION_MS = 60 * 60 * 1000;

    @Override
    public void blacklistToken(String token) {
        long expiryTime = System.currentTimeMillis() + EXPIRY_DURATION_MS;
        blacklist.put(token, expiryTime);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        Long expiryTime = blacklist.get(token);

        if (expiryTime == null) return false;

        // Remove expired entries automatically
        if (System.currentTimeMillis() > expiryTime) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }
}
