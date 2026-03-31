package com.anonchat.user.controller;

import com.anonchat.user.dto.RegisterUserRequestDto;
import com.anonchat.user.dto.UserResponseDto;
import com.anonchat.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User controller exposing user management endpoints.
 * Registration is public; profile endpoints require authentication.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * POST /api/v1/users/register
     * Register a new user.
     * All inputs are validated and password is hashed server-side.
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterUserRequestDto request) {
        log.info("User registration request received");
        UserResponseDto response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/users/{userId}
     * Get user profile by ID.
     * Requires valid JWT authentication.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId) {
        log.debug("User profile request for ID: {}", userId);
        UserResponseDto response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/users/username/{username}
     * Get user profile by username.
     * Requires valid JWT authentication.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username) {
        log.debug("User profile request for username: {}", username);
        UserResponseDto response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }
}
