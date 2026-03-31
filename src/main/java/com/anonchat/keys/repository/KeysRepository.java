package com.anonchat.keys.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Keys module.
 */
@Repository
public interface KeysRepository extends JpaRepository<Object, String> {
    // Keys repository methods will be defined here
}
