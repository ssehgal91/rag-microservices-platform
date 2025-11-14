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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private SessionMapper sessionMapper;

    @InjectMocks
    private SessionServiceImpl sessionService;

    private Session session;
    private Session saved;
    private SessionResponse response;
    private UUID sessionId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        sessionId = UUID.randomUUID();

        session = new Session();
        session.setId(sessionId);
        session.setUserId("user123");
        session.setTitle("Old title");
        session.setFavorite(false);
        session.setCreatedAt(OffsetDateTime.now());
        session.setUpdatedAt(OffsetDateTime.now());

        saved = new Session();
        saved.setId(sessionId);
        saved.setUserId("user123");
        saved.setTitle("New Title");
        saved.setFavorite(false);
        saved.setCreatedAt(OffsetDateTime.now());
        saved.setUpdatedAt(OffsetDateTime.now());

        // Build SessionResponse using setters
        response = new SessionResponse();
        response.setId(sessionId);
        response.setUserId("user123");
        response.setTitle("New Title");
        response.setFavorite(false);
        response.setCreatedAt(saved.getCreatedAt());
        response.setUpdatedAt(saved.getUpdatedAt());
    }

    // -----------------------------------------------------------
    // createSession()
    // -----------------------------------------------------------
    @Test
    void createSession_ShouldSaveSession() {
        CreateSessionRequest request = new CreateSessionRequest();
        request.setUserId("user123");
        request.setTitle("New Title");

        when(sessionMapper.toEntity(request)).thenReturn(session);
        when(sessionRepository.save(session)).thenReturn(saved);
        when(sessionMapper.toResponse(saved)).thenReturn(response);

        SessionResponse result = sessionService.createSession(request);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        verify(sessionRepository).save(session);
    }

    // -----------------------------------------------------------
    // renameSession()
    // -----------------------------------------------------------
    @Test
    void renameSession_ShouldUpdateTitle_WhenExists() {
        RenameSessionRequest req = new RenameSessionRequest();
        req.setTitle("Updated Title");

        saved.setTitle("Updated Title");
        response.setTitle("Updated Title");

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(saved);
        when(sessionMapper.toResponse(saved)).thenReturn(response);

        SessionResponse result = sessionService.renameSession(sessionId, req);

        assertEquals("Updated Title", result.getTitle());
        verify(sessionRepository).save(session);
    }

    @Test
    void renameSession_ShouldThrow_WhenNotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        RenameSessionRequest req = new RenameSessionRequest();
        req.setTitle("new title");

        assertThrows(ChatSessionNotFoundException.class,
                () -> sessionService.renameSession(sessionId, req));
    }

    // -----------------------------------------------------------
    // deleteSession()
    // -----------------------------------------------------------
    @Test
    void deleteSession_ShouldDelete_WhenExists() {
        when(sessionRepository.existsById(sessionId)).thenReturn(true);

        sessionService.deleteSession(sessionId);

        verify(sessionRepository).deleteById(sessionId);
    }

    @Test
    void deleteSession_ShouldThrow_WhenNotFound() {
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        assertThrows(ChatSessionNotFoundException.class,
                () -> sessionService.deleteSession(sessionId));

        verify(sessionRepository, never()).deleteById(any());
    }

    // -----------------------------------------------------------
    // toggleFavorite()
    // -----------------------------------------------------------
    @Test
    void toggleFavorite_ShouldToggle_WhenExists() {
        ToggleFavoriteRequest req = new ToggleFavoriteRequest();
        req.setFavorite(true);

        session.setFavorite(true);
        saved.setFavorite(true);
        response.setFavorite(true);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(sessionRepository.save(session)).thenReturn(saved);
        when(sessionMapper.toResponse(saved)).thenReturn(response);

        SessionResponse result = sessionService.toggleFavorite(sessionId, req);

        assertTrue(result.isFavorite());
    }

    @Test
    void toggleFavorite_ShouldThrow_WhenNotFound() {
        ToggleFavoriteRequest req = new ToggleFavoriteRequest();
        req.setFavorite(true);

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> sessionService.toggleFavorite(sessionId, req));
    }

    // -----------------------------------------------------------
    // getSessionsForUser()
    // -----------------------------------------------------------
    @Test
    void getSessionsForUser_ShouldReturnList() {
        List<Session> list = List.of(saved);
        List<SessionResponse> mapped = List.of(response);

        when(sessionRepository.findByUserIdOrderByUpdatedAtDesc("user123")).thenReturn(list);
        when(sessionMapper.toResponseList(list)).thenReturn(mapped);

        List<SessionResponse> result = sessionService.getSessionsForUser("user123");

        assertEquals(1, result.size());
    }

    // -----------------------------------------------------------
    // getAllSessions()
    // -----------------------------------------------------------
    @Test
    void getAllSessions_ShouldReturnAll() {
        List<Session> list = List.of(saved);
        List<SessionResponse> mapped = List.of(response);

        when(sessionRepository.findAllByOrderByUpdatedAtDesc()).thenReturn(list);
        when(sessionMapper.toResponseList(list)).thenReturn(mapped);

        List<SessionResponse> result = sessionService.getAllSessions();

        assertEquals(1, result.size());
    }

    // -----------------------------------------------------------
    // getSessionById()
    // -----------------------------------------------------------
    @Test
    void getSessionById_ShouldReturnSession_WhenExists() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(saved));
        when(sessionMapper.toResponse(saved)).thenReturn(response);

        SessionResponse result = sessionService.getSessionById(sessionId);

        assertEquals(sessionId, result.getId());
    }

    @Test
    void getSessionById_ShouldThrow_WhenNotFound() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(ChatSessionNotFoundException.class,
                () -> sessionService.getSessionById(sessionId));
    }
}
