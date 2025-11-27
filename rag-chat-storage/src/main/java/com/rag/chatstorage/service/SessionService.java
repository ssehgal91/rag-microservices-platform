package com.rag.chatstorage.service;

import com.rag.chatstorage.dto.CreateSessionRequest;
import com.rag.chatstorage.dto.RenameSessionRequest;
import com.rag.chatstorage.dto.SessionResponse;
import com.rag.chatstorage.dto.ToggleFavoriteRequest;

import java.util.List;
import java.util.UUID;

public interface SessionService {

    /**
     * Creates a new chat session.
     *
     * @param request DTO containing user ID and session title.
     * @return the created {@link SessionResponse}.
     */
    SessionResponse createSession(CreateSessionRequest request);

    /**
     * Deletes a chat session by ID.
     *
     * @param sessionId UUID of the session.
     */
    void deleteSession(UUID sessionId);

    /**
     * Renames an existing session.
     *
     * @param sessionId UUID of the session to rename.
     * @param request DTO containing new title.
     * @return updated {@link SessionResponse}.
     */
    SessionResponse renameSession(UUID sessionId, RenameSessionRequest request);

   // List<SessionResponse> getAllSessions(String userId);

    /**
     * Toggles the favorite status of a session.
     *
     * @param sessionId UUID of the session.
     * @param request DTO containing favorite status.
     * @return updated {@link SessionResponse}.
     */
    SessionResponse toggleFavorite(UUID sessionId, ToggleFavoriteRequest request);

    /**
     * Retrieves all sessions for a specific user.
     */
    List<SessionResponse> getSessionsForUser(String userId);

    /**
     * Retrieves all sessions across all users.
     */
    List<SessionResponse> getAllSessions();

    /**
     * Retrieves a single session by its ID.
     */
    SessionResponse getSessionById(UUID sessionId);
}
