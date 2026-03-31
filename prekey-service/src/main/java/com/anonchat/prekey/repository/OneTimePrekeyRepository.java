package com.anonchat.prekey.repository;

import com.anonchat.prekey.entity.OneTimePrekey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for OneTimePrekey entity.
 */
@Repository
public interface OneTimePrekeyRepository extends JpaRepository<OneTimePrekey, String> {
    List<OneTimePrekey> findByUserId(String userId);
    
    Optional<OneTimePrekey> findByUserIdAndOtPrekeyId(String userId, Integer otPrekeyId);
    
    List<OneTimePrekey> findByUserIdAndIsUsedFalse(String userId);
    
    long countByUserIdAndIsUsedFalse(String userId);
}
