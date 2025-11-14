package com.rag.chatstorage.service.impl;

import com.rag.chatstorage.dto.CreateSessionRequest;
import com.rag.chatstorage.dto.RenameSessionRequest;
import com.rag.chatstorage.dto.SessionResponse;
import com.rag.chatstorage.dto.ToggleFavoriteRequest;
import com.rag.chatstorage.entity.Session;
import com.rag.chatstorage.exception.ChatSessionNotFoundException;
import com.rag.chatstorage.exception.ResourceNotFoundException;
import com.rag.chatstorage.mapper.SessionMapper;
import com.rag.chatstorage.repository.SessionRepository;
import com.rag.chatstorage.service.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    
    /**
     * Creates a new chat session based on the provided request DTO.
     *
     * @param request DTO containing user ID and title.
     * @return {@link SessionResponse} representing the newly created session.
     */
    @Override
    public SessionResponse createSession(CreateSessionRequest request) {
        try {
            Session session = sessionMapper.toEntity(request);
            session.setCreatedAt(OffsetDateTime.now());
            session.setUpdatedAt(OffsetDateTime.now());
            Session saved = sessionRepository.save(session);
            log.info("Created new session [{}] for user [{}]", saved.getId(), saved.getUserId());
            return sessionMapper.toResponse(saved);
        } catch (Exception ex) {
            log.error("Database error while creating session for user [{}]: {}", request.getUserId(), ex.getMessage(), ex);
            throw ex; // handled by GlobalExceptionHandler
        }
    }

    /**
     * Renames an existing session.
     *
     * @param sessionId the UUID of the session to rename.
     * @param request the {@link RenameSessionRequest} DTO containing the new title.
     * @return the updated {@link SessionResponse}.
     * @throws ResourceNotFoundException if session not found.
     */
    @Override
    public SessionResponse renameSession(UUID sessionId, RenameSessionRequest request) {
        try {
        // Fetch Session with ID
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ChatSessionNotFoundException("Session not found with ID: " + sessionId));

        session.setTitle(request.getTitle());
        // session.setUpdatedAt(OffsetDateTime.now());
        return sessionMapper.toResponse(sessionRepository.save(session));
        } catch (ChatSessionNotFoundException e) {
            log.warn("RenameSession validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while renaming session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void deleteSession(UUID sessionId) {
        try {
            if (!sessionRepository.existsById(sessionId)) {
                throw new ChatSessionNotFoundException("Chat session not found with id: " + sessionId);
            }

            sessionRepository.deleteById(sessionId);
            // log.info("Deleted session [{}] and {} messages", sessionId, deletedMessagesCount);

        } catch (ChatSessionNotFoundException e) {
            log.warn("DeleteSession validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while deleting session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public SessionResponse  toggleFavorite(UUID sessionId, ToggleFavoriteRequest request) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));

        session.setFavorite(request.isFavorite());
        session.setUpdatedAt(OffsetDateTime.now());
        Session updated = sessionRepository.save(session);

        log.info("Updated favorite status of session [{}] to {}", sessionId, request.isFavorite());
        return sessionMapper.toResponse(updated);
    }

    @Override
    public List<SessionResponse> getSessionsForUser(String userId) {
        List<Session> sessions =
                sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId);

        return sessionMapper.toResponseList(sessions);
    }

    @Override
    public List<SessionResponse> getAllSessions() {
        List<Session> sessions =
                sessionRepository.findAllByOrderByUpdatedAtDesc();

        return sessionMapper.toResponseList(sessions);
    }

    @Override
    public SessionResponse getSessionById(UUID sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ChatSessionNotFoundException("Session not found with ID: " + sessionId));

        return sessionMapper.toResponse(session);
    }

}
