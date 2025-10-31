package com.enterprise.workforce.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "MyUltraSecureSecretKeyForEnterpriseWorkforceSystem@2025";

    //  Store blacklisted (logged-out) tokens temporarily (in-memory)
    // You can later move this to Redis or a database for distributed systems
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //  Add token to blacklist
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    //  Check if token is blacklisted
    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + role.replace("ROLE_", ""));
        return createToken(claims, username);
    }

    public String generateRefreshToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + role.replace("ROLE_", ""));
        return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7)) // 7 days
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 10)) // 10 hours
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }
    public long getExpiration(String token) {
        return extractClaim(token, Claims::getExpiration).getTime();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token)
                && !isTokenInvalidated(token)); //  block blacklisted tokens
    }

}
