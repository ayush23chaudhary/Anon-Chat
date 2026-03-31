package com.anonchat.messaging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Messaging module.
 */
@Repository
public interface MessagingRepository extends JpaRepository<Object, String> {
    // Messaging repository methods will be defined here
}
