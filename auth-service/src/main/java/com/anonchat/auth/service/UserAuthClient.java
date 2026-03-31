package com.anonchat.auth.service;

import java.util.Optional;

/**
 * Client interface for user service integration.
 * Abstracts user lookup for authentication purposes.
 * Implementation can use RestTemplate, WebClient, or direct service calls.
 */
public interface UserAuthClient {
    
    /**
     * Fetch user credentials by username.
     */
    Optional<UserCredentials> getUserCredentials(String username);
    
    /**
     * Fetch user by ID.
     */
    Optional<UserCredentials> getUserById(String userId);

    /**
     * Create a new user with username and hashed password.
     */
    UserCredentials createUser(String username, String hashedPassword);
}
