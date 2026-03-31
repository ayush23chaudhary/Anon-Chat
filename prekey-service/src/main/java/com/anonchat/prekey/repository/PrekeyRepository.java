package com.anonchat.prekey.repository;

import com.anonchat.prekey.entity.Prekey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Prekey entity.
 */
@Repository
public interface PrekeyRepository extends JpaRepository<Prekey, String> {
    List<Prekey> findByUserId(String userId);
    
    Optional<Prekey> findByUserIdAndPrekeyId(String userId, Integer prekeyId);
    
    @Query("SELECT p FROM Prekey p WHERE p.userId = :userId AND p.isUsed = false ORDER BY p.createdAt DESC LIMIT 1")
    Optional<Prekey> findLatestUnusedPrekey(String userId);
    
    List<Prekey> findByUserIdAndIsUsedFalse(String userId);
    
    long countByUserIdAndIsUsedFalse(String userId);
}
