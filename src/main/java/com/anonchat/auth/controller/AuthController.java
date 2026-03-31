package com.anonchat.auth.controller;

import com.anonchat.auth.dto.AuthResponse;
import com.anonchat.auth.dto.LoginRequest;
import com.anonchat.auth.dto.RegisterRequest;
import com.anonchat.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController.
 * 
 * REST endpoints for authentication operations.
 * Base path: /api/auth
 * 
 * Endpoints:
 * - POST /register - Register new user
 * - POST /login - Login user
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
     * Register new user.
     * 
     * POST /api/auth/register
     * 
     * @param request Registration request (username, identityPublicKey)
     * @return Authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register endpoint called for username: {}", request.getUsername());

        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            log.warn("Registration validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Login user.
     * 
     * POST /api/auth/login
     * 
     * @param request Login request (username)
     * @return Authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login endpoint called for username: {}", request.getUsername());

        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Login validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
