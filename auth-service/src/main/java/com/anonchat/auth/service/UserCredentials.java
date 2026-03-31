package com.anonchat.auth.service;

import lombok.Builder;
import lombok.Data;

/**
 * User credentials model for authentication.
 */
@Data
@Builder
public class UserCredentials {
    private String id;
    private String username;
    private String passwordHash;
}
