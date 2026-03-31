package com.anonchat.user.repository;

import com.anonchat.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * UserRepository.
 * 
 * Data access layer for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by username.
     * 
     * @param username User's username
     * @return Optional containing User if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if username exists.
     * 
     * @param username Username to check
     * @return true if username is taken
     */
    boolean existsByUsername(String username);
}
