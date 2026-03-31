package com.anonchat.auth.service;

import com.anonchat.auth.dto.AuthResponse;
import com.anonchat.auth.dto.LoginRequest;
import com.anonchat.auth.dto.RegisterRequest;
import com.anonchat.auth.jwt.JwtService;
import com.anonchat.user.entity.User;
import com.anonchat.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * AuthService.
 * 
 * Core authentication business logic for anonymous-first system.
 * 
 * Features:
 * - User registration with identity public key
 * - User login without password
 * - JWT token generation
 * 
 * Security:
 * - No passwords stored
 * - Username must be unique
 * - Identity keys stored for encryption setup
 */
@Slf4j
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user.
     * 
     * Creates user account with identity public key.
     * 
     * @param request Registration request with username and public key
     * @return Authentication response with JWT token
     * @throws IllegalArgumentException if username already exists
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for username: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        try {
            // Create new user
            User user = User.builder()
                    .username(request.getUsername())
                    .identityPublicKey(request.getIdentityPublicKey())
                    .displayName(request.getDisplayName())
                    .active(true)
                    .build();

            // Save user to database
            User savedUser = userRepository.save(user);
            log.info("User registered successfully: {}", savedUser.getId());

            // Generate JWT token
            String token = jwtService.generateToken(savedUser.getId(), savedUser.getUsername());

            return AuthResponse.builder()
                    .accessToken(token)
                    .userId(savedUser.getId().toString())
                    .username(savedUser.getUsername())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getTokenExpiryMs() / 1000)
                    .build();

        } catch (Exception e) {
            log.error("Registration error: {}", e.getMessage());
            throw new RuntimeException("Registration failed", e);
        }
    }

    /**
     * Login user.
     * 
     * Authenticates user by username and generates JWT token.
     * 
     * @param request Login request with username
     * @return Authentication response with JWT token
     * @throws IllegalArgumentException if user not found
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        try {
            // Find user by username
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        log.warn("Login failed - user not found: {}", request.getUsername());
                        return new IllegalArgumentException("User not found");
                    });

            // Check if account is active
            if (!user.isActive()) {
                log.warn("Login failed - account inactive: {}", request.getUsername());
                throw new IllegalArgumentException("Account is inactive");
            }

            log.info("User logged in successfully: {}", user.getId());

            // Generate JWT token
            String token = jwtService.generateToken(user.getId(), user.getUsername());

            return AuthResponse.builder()
                    .accessToken(token)
                    .userId(user.getId().toString())
                    .username(user.getUsername())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getTokenExpiryMs() / 1000)
                    .build();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            throw new RuntimeException("Login failed", e);
        }
    }

    /**
     * Get user by ID.
     * 
     * @param userId User ID
     * @return User if found
     * @throws IllegalArgumentException if user not found
     */
    @Transactional(readOnly = true)
    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Validate user exists and is active.
     * 
     * @param userId User ID
     * @return true if user exists and is active
     */
    @Transactional(readOnly = true)
    public boolean isUserActive(UUID userId) {
        return userRepository.findById(userId)
                .map(User::isActive)
                .orElse(false);
    }
}
