package com.anonchat.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Authentication module.
 * Package for auth-related repository interfaces.
 */
@Repository
public interface AuthRepository extends JpaRepository<Object, String> {
    // Auth repository methods will be defined here
}
