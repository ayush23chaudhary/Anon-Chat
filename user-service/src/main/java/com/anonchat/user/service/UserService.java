package com.anonchat.user.service;

import com.anonchat.common.exception.ValidationException;
import com.anonchat.user.dto.RegisterUserRequestDto;
import com.anonchat.user.dto.UserResponseDto;
import com.anonchat.user.entity.User;
import com.anonchat.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User service for user management operations.
 * Handles registration, profile updates, and user lookups.
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user.
     * Validates input and hashes password before storing.
     * Security Note: Password is NEVER stored in plaintext.
     */
    @Transactional
    public UserResponseDto registerUser(RegisterUserRequestDto request) {
        log.info("User registration attempt");

        // Validate username availability
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists", "USERNAME_EXISTS");
        }

        // Validate email availability
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists", "EMAIL_EXISTS");
        }

        // Hash password - MUST be done here, never transmitted in plaintext
        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .displayName(request.getDisplayName())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getId());

        return toResponseDto(savedUser);
    }

    /**
     * Get user by ID.
     */
    public UserResponseDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.anonchat.common.exception.ResourceNotFoundException(
                        "User not found", "USER_NOT_FOUND"));
        return toResponseDto(user);
    }

    /**
     * Get user by username.
     */
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new com.anonchat.common.exception.ResourceNotFoundException(
                        "User not found", "USER_NOT_FOUND"));
        return toResponseDto(user);
    }

    /**
     * Internal method: Get user credentials for authentication.
     * This is used by auth service - returns password hash for comparison.
     * Security Note: This method should only be called from auth service.
     */
    public User getUserCredentialsForAuth(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    /**
     * Check if username exists.
     */
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Convert User entity to response DTO.
     * Ensures password hash is never exposed.
     */
    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
