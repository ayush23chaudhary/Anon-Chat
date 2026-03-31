package com.anonchat.auth.jwt;

import io.jsonwebtoken.Claims;
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
import java.util.UUID;

/**
 * JwtService - Production-ready JWT utility for Spring Boot 3.
 * 
 * Handles JWT token generation, validation, and claims extraction.
 * Security Notes:
 * - Uses HS512 algorithm with 256-bit keys
 * - Tokens include userId and username in claims
 * - Token expiry is configurable
 * - Never logs token values (only operation results)
 */
@Slf4j
@Component
public class JwtService {

    private final SecretKey secretKey;
    private final long tokenExpiryMs;

    /**
     * Initialize JWT service with secret and expiry from configuration.
     * 
     * @param jwtSecret JWT signing secret (must be 256+ bits)
     * @param tokenExpirySeconds Token expiry time in seconds
     */
    public JwtService(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiry-seconds:3600}") long tokenExpirySeconds) {
        
        // Validate secret length (minimum 32 characters for HS512)
        if (jwtSecret.length() < 32) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 characters (256 bits) for HS512");
        }
        
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.tokenExpiryMs = tokenExpirySeconds * 1000;
        
        log.info("JWT service initialized with {} second expiry", tokenExpirySeconds);
    }

    /**
     * Generate JWT token for a user.
     * 
     * @param userId User UUID
     * @param username User's display name or identifier
     * @return JWT token string
     */
    public String generateToken(UUID userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpiryMs);

        try {
            String token = Jwts.builder()
                    .setSubject(userId.toString())
                    .claim("username", username)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();

            log.debug("Token generated for user: {}", userId);
            return token;

        } catch (Exception e) {
            log.error("Failed to generate token: {}", e.getMessage());
            throw new RuntimeException("Token generation failed", e);
        }
    }

    /**
     * Extract user ID from JWT token.
     * 
     * @param token JWT token string
     * @return User UUID from token subject
     * @throws JwtException if token is invalid or expired
     */
    public UUID extractUserId(String token) {
        try {
            Claims claims = parseToken(token);
            String userId = claims.getSubject();
            return UUID.fromString(userId);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid user ID format in token");
            throw new JwtException("Invalid user ID in token", e);
        }
    }

    /**
     * Extract username from JWT token.
     * 
     * @param token JWT token string
     * @return Username from token claims
     * @throws JwtException if token is invalid or expired
     */
    public String extractUsername(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("username");
    }

    /**
     * Validate JWT token.
     * Checks signature, expiration, and format.
     * 
     * @param token JWT token string
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            log.debug("Token validation successful");
            return true;

        } catch (JwtException e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return false;

        } catch (Exception e) {
            log.warn("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Parse and validate JWT token.
     * Internal method - extracts claims after validation.
     * 
     * @param token JWT token string
     * @return Claims from token
     * @throws JwtException if token is invalid or expired
     */
    private Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.debug("Invalid token signature");
            throw new JwtException("Invalid token signature", e);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.debug("Token has expired");
            throw new JwtException("Token has expired", e);

        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.debug("Malformed token");
            throw new JwtException("Malformed token", e);

        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.debug("Unsupported token");
            throw new JwtException("Unsupported token", e);

        } catch (Exception e) {
            log.debug("Token parsing error: {}", e.getMessage());
            throw new JwtException("Token parsing error", e);
        }
    }

    /**
     * Get token expiry time in milliseconds.
     * Useful for client-side token refresh logic.
     * 
     * @return Token expiry time in milliseconds
     */
    public long getTokenExpiryMs() {
        return tokenExpiryMs;
    }
}
