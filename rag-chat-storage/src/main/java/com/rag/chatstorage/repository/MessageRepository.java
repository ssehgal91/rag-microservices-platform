package com.rag.chatstorage.repository;

import com.rag.chatstorage.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findBySessionIdOrderByCreatedAtAsc(UUID sessionId, Pageable pageable);
}
