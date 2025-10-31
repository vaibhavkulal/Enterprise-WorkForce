package com.enterprise.workforce.service.impl;

import com.enterprise.workforce.entity.RefreshToken;
import com.enterprise.workforce.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    private final Duration refreshDuration = Duration.ofDays(30);

    public RefreshToken createToken(String username){
        RefreshToken t = new RefreshToken();
        t.setToken(UUID.randomUUID().toString());
        t.setUsername(username);
        t.setExpiryDate(Instant.now().plus(refreshDuration));
        return repo.save(t);
    }

    public void revoke(String token){
        repo.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            repo.save(rt);
        });
    }

    public void revokeAllForUser(String username){
        repo.findByUsernameAndRevokedFalse(username)
            .forEach(rt -> { rt.setRevoked(true); repo.save(rt); });
    }

    public boolean validate(String token){
        return repo.findByToken(token)
            .filter(t -> !t.isRevoked() && t.getExpiryDate().isAfter(Instant.now()))
            .isPresent();
    }
}
