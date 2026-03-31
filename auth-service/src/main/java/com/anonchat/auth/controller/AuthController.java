package com.anonchat.auth.controller;

import com.anonchat.auth.dto.LoginRequestDto;
import com.anonchat.auth.dto.RefreshTokenRequestDto;
import com.anonchat.auth.dto.TokenResponseDto;
import com.anonchat.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Auth controller exposing authentication endpoints.
 * All endpoints are public (no authentication required).
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/auth/register
     * Register a new user with username and password.
     * Returns access token on success.
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@Valid @RequestBody LoginRequestDto request) {
        log.info("Registration request received");
        TokenResponseDto response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Authenticate user with credentials.
     * Request body must contain username and password.
     * Returns access and refresh tokens on success.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        log.info("Login request received");
        TokenResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/refresh
     * Refresh access token using refresh token.
     * Refresh token must be valid and not expired.
     * Returns new access and refresh tokens.
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.debug("Token refresh request received");
        TokenResponseDto response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/validate
     * Validate if current JWT token is valid.
     * Requires Bearer token in Authorization header.
     * Returns 200 if valid, 401 if invalid.
     */
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        boolean isValid = authService.validateToken(token);

        return isValid ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
