package com.anonchat.auth.service;

import com.anonchat.auth.config.JwtTokenProvider;
import com.anonchat.auth.dto.LoginRequestDto;
import com.anonchat.auth.dto.RefreshTokenRequestDto;
import com.anonchat.auth.dto.TokenResponseDto;
import com.anonchat.common.constant.SecurityConstants;
import com.anonchat.common.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Auth service handling authentication operations.
 * Integrates with user service for credential verification.
 */
@Slf4j
@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthClient userAuthClient;

    public AuthService(
            JwtTokenProvider jwtTokenProvider,
            PasswordEncoder passwordEncoder,
            UserAuthClient userAuthClient
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userAuthClient = userAuthClient;
    }

    /**
     * Register a new user with a random ID (no database interaction).
     * For demo purposes - generates a random user ID without persistence.
     */
    public TokenResponseDto register(LoginRequestDto request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        
        try {
            // Generate random user ID
            String randomUserId = java.util.UUID.randomUUID().toString();
            
            // Generate tokens with random ID
            String accessToken = jwtTokenProvider.generateAccessToken(randomUserId, request.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(randomUserId);

            log.info("User registered with random ID: {} (username: {})", randomUserId, request.getUsername());
            return TokenResponseDto.of(accessToken, refreshToken, SecurityConstants.ACCESS_TOKEN_EXPIRY_MS);

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            throw new AuthenticationException("Registration failed", "REGISTRATION_FAILED");
        }
    }

    /**
     * Authenticate user credentials and generate tokens.
     * Security Note: Use a separate UserAuthClient/service to verify credentials.
     * Server must NOT perform cryptographic validation of user input.
     */
    public TokenResponseDto login(LoginRequestDto request) {
        log.info("Authentication attempt for user");
        
        try {
            // Fetch user and verify credentials (delegated to user service)
            var userCredentials = userAuthClient.getUserCredentials(request.getUsername());

            if (!userCredentials.isPresent()) {
                // Don't reveal if user exists - use generic message
                throw new AuthenticationException("Authentication failed", "AUTH_FAILED");
            }

            var user = userCredentials.get();
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                throw new AuthenticationException("Authentication failed", "AUTH_FAILED");
            }

            // Generate tokens
            String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

            log.info("User authenticated successfully");
            return TokenResponseDto.of(accessToken, refreshToken, SecurityConstants.ACCESS_TOKEN_EXPIRY_MS);

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new AuthenticationException("Authentication failed", "AUTH_FAILED");
        }
    }

    /**
     * Refresh access token using refresh token.
     */
    public TokenResponseDto refreshToken(RefreshTokenRequestDto request) {
        log.debug("Token refresh request");

        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new AuthenticationException("Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        if (userId == null) {
            throw new AuthenticationException("Invalid refresh token", "INVALID_REFRESH_TOKEN");
        }

        // Fetch user from user service to get current username
        var user = userAuthClient.getUserById(userId)
                .orElseThrow(() -> new AuthenticationException("User not found", "USER_NOT_FOUND"));

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        log.debug("Token refreshed successfully");
        return TokenResponseDto.of(newAccessToken, newRefreshToken, SecurityConstants.ACCESS_TOKEN_EXPIRY_MS);
    }

    /**
     * Validate a JWT token.
     * Used for internal authentication checks.
     */
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    /**
     * Extract userId from a valid JWT token.
     */
    public String getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
