package com.anonchat.messaging.repository;

import com.anonchat.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Message entity.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
    List<Message> findBySenderIdAndRecipientId(String senderId, String recipientId);
    
    List<Message> findByRecipientIdAndIsDeliveredFalse(String recipientId);
    
    @Query("SELECT m FROM Message m WHERE m.recipientId = :recipientId ORDER BY m.createdAt DESC LIMIT :limit")
    List<Message> findRecentMessagesForRecipient(String recipientId, int limit);
    
    long countByRecipientIdAndIsDeliveredFalse(String recipientId);
}
