package com.anonchat.auth.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT token provider for token generation and validation.
 * Security Note: Never expose secret key. Always use environment variables for secrets.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry:900000}") long accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry:604800000}") long refreshTokenExpiry
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    /**
     * Generate JWT access token.
     * Contains only essential claims for authentication.
     */
    public String generateAccessToken(String userId, String username) {
        return generateToken(userId, username, accessTokenExpiry);
    }

    /**
     * Generate JWT refresh token.
     * Contains minimal claims, used only for token refresh.
     */
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return generateTokenWithClaims(claims, userId, refreshTokenExpiry);
    }

    private String generateToken(String userId, String username, long expiryMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        return generateTokenWithClaims(claims, userId, expiryMs);
    }

    private String generateTokenWithClaims(Map<String, Object> claims, String subject, long expiryMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiryMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract userId from JWT token.
     * Returns null if token is invalid.
     */
    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            log.debug("Failed to extract userId from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Validate JWT token.
     * Returns true if token is valid and not expired.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}
