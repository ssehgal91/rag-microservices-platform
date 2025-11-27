package com.rag.chatstorage.repository;

import java.util.List;
import java.util.UUID;

import com.rag.chatstorage.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    List<Session> findByUserIdOrderByUpdatedAtDesc(String userId);
    List<Session> findAllByOrderByUpdatedAtDesc();
}
