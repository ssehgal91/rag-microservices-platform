package com.rag.chatstorage.service.impl;


import com.rag.chatstorage.dto.MessageRequest;
import com.rag.chatstorage.dto.MessageResponse;
import com.rag.chatstorage.entity.Message;
import com.rag.chatstorage.entity.Session;
import com.rag.chatstorage.exception.ChatSessionNotFoundException;
import com.rag.chatstorage.mapper.MessageMapper;
import com.rag.chatstorage.repository.MessageRepository;
import com.rag.chatstorage.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageServiceImplTest {
    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    private UUID sessionId;
    private Session mockSession;
    private MessageRequest request;
    private Message messageEntity;
    private Message savedMessage;
    private MessageResponse responseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sessionId = UUID.randomUUID();

        mockSession = new Session();
        mockSession.setId(sessionId);

        request = new MessageRequest();
        request.setContent("Hello world");

        messageEntity = new Message();
        messageEntity.setContent("Hello world");
        messageEntity.setSession(mockSession);

        savedMessage = new Message();
        savedMessage.setId(UUID.randomUUID());
        savedMessage.setContent("Hello world");
        savedMessage.setCreatedAt(OffsetDateTime.now());
        savedMessage.setSession(mockSession);

        responseDto = new MessageResponse();
        responseDto.setId(savedMessage.getId());
        responseDto.setSessionId(sessionId);
        responseDto.setSender("user123");
        responseDto.setContent("Hello world");
        responseDto.setContext(null);
        responseDto.setCreatedAt(savedMessage.getCreatedAt());
    }

    // ------------------------------------------------------
    // TEST 1: addMessage() SUCCESS
    // ------------------------------------------------------
    @Test
    void addMessage_ShouldSaveMessage_WhenSessionExists() {
        // Arrange
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(messageMapper.toEntity(request, mockSession)).thenReturn(messageEntity);
        when(messageRepository.save(messageEntity)).thenReturn(savedMessage);
        when(messageMapper.toResponse(savedMessage)).thenReturn(responseDto);

        // Act
        MessageResponse result = messageService.addMessage(sessionId, request);

        // Assert
        assertNotNull(result);
        assertEquals(savedMessage.getId(), result.getId());
        assertEquals("Hello world", result.getContent());

        verify(sessionRepository, times(1)).findById(sessionId);
        verify(messageRepository, times(1)).save(messageEntity);
        verify(messageMapper, times(1)).toResponse(savedMessage);
    }

    // ------------------------------------------------------
    // TEST 2: addMessage() → SESSION NOT FOUND
    // ------------------------------------------------------
    @Test
    void addMessage_ShouldThrowException_WhenSessionDoesNotExist() {
        when(sessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        ChatSessionNotFoundException ex = assertThrows(
                ChatSessionNotFoundException.class,
                () -> messageService.addMessage(sessionId, request)
        );

        assertTrue(ex.getMessage().contains("Chat session not found"));

        verify(sessionRepository, times(1)).findById(sessionId);
        verify(messageRepository, never()).save(any());
    }

    // ------------------------------------------------------
    // TEST 3: getMessagesBySessionId() SUCCESS
    // ------------------------------------------------------
    @Test
    void getMessagesBySessionId_ShouldReturnPagedMessages_WhenSessionExists() {
        List<Message> messageList = List.of(savedMessage);
        Page<Message> page = new PageImpl<>(messageList);

        when(sessionRepository.existsById(sessionId)).thenReturn(true);
        when(messageRepository.findBySessionIdOrderByCreatedAtAsc(eq(sessionId), any(PageRequest.class)))
                .thenReturn(page);
        when(messageMapper.toResponse(savedMessage)).thenReturn(responseDto);

        Page<MessageResponse> result = messageService.getMessagesBySessionId(sessionId, 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(savedMessage.getId(), result.getContent().get(0).getId());
    }

    // ------------------------------------------------------
    // TEST 4: getMessagesBySessionId() → SESSION NOT FOUND
    // ------------------------------------------------------
    @Test
    void getMessagesBySessionId_ShouldThrowException_WhenSessionDoesNotExist() {
        when(sessionRepository.existsById(sessionId)).thenReturn(false);

        assertThrows(ChatSessionNotFoundException.class,
                () -> messageService.getMessagesBySessionId(sessionId, 0, 10));

        verify(messageRepository, never()).findBySessionIdOrderByCreatedAtAsc(any(), any());
    }

}
