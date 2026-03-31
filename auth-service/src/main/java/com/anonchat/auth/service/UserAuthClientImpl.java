package com.anonchat.auth.service;

import com.anonchat.user.entity.User;
import com.anonchat.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * UserAuthClient implementation using UserRepository.
 * Handles user creation and lookup for authentication.
 */
@Slf4j
@Service
public class UserAuthClientImpl implements UserAuthClient {

    private final UserRepository userRepository;

    public UserAuthClientImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserCredentials> getUserCredentials(String username) {
        log.debug("Fetching user credentials for username: {}", username);
        return userRepository.findByUsername(username)
                .map(user -> UserCredentials.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .passwordHash(user.getPasswordHash())
                        .build());
    }

    @Override
    public Optional<UserCredentials> getUserById(String userId) {
        log.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .map(user -> UserCredentials.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .passwordHash(user.getPasswordHash())
                        .build());
    }

    @Override
    public UserCredentials createUser(String username, String hashedPassword) {
        log.info("Creating new user: {}", username);
        
        // Generate email from username
        String email = username + "@anonchat.local";
        
        User newUser = User.builder()
                .username(username)
                .email(email)
                .passwordHash(hashedPassword)
                .displayName(username)
                .build();

        User savedUser = userRepository.save(newUser);
        
        log.info("User created successfully: {} (ID: {})", username, savedUser.getId());
        
        return UserCredentials.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .passwordHash(savedUser.getPasswordHash())
                .build();
    }
}
