package com.rag.chatstorage.service.impl;

import com.rag.chatstorage.dto.MessageRequest;
import com.rag.chatstorage.dto.MessageResponse;
import com.rag.chatstorage.entity.Message;
import com.rag.chatstorage.entity.Session;
import com.rag.chatstorage.exception.ChatSessionNotFoundException;
import com.rag.chatstorage.mapper.MessageMapper;
import com.rag.chatstorage.repository.MessageRepository;
import com.rag.chatstorage.repository.SessionRepository;
import com.rag.chatstorage.service.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional // Ensures consistency between session check and message save
    public MessageResponse addMessage(UUID sessionId, MessageRequest chatMessageRequest) {
        try {
            // Validate the session existence
            Session chatSession = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new ChatSessionNotFoundException(
                            "Chat session not found with id: " + sessionId));

            // Map DTO to Entity using MapStruct
            Message message = messageMapper.toEntity(chatMessageRequest, chatSession);

            // Save the message
            Message savedMessage = messageRepository.save(message);
            log.info("Message [{}] added to session [{}]", savedMessage.getId(), sessionId);

            // Return DTO
            return messageMapper.toResponse(savedMessage);

        } catch (ChatSessionNotFoundException e) {
            log.warn("AddMessage validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while adding message to session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<MessageResponse> getMessagesBySessionId(UUID sessionId, int page, int size) {
        try {
            // Verify that the session exists before querying
            if (!sessionRepository.existsById(sessionId)) {
                throw new ChatSessionNotFoundException("Chat session not found with id: " + sessionId);
            }

            // Create a pageable object for pagination
            PageRequest pageable = PageRequest.of(page, size);

            // Fetch messages using repository method
            Page<Message> messages = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId, pageable);
            log.debug("Fetched {} messages for session [{}]", messages.getTotalElements(), sessionId);

            // Convert Page<Message> to Page<MessageResponse> using Page.map()
            return messages.map(messageMapper::toResponse);

        } catch (ChatSessionNotFoundException e) {
            log.warn("GetMessagesBySession validation error for session [{}]: {}", sessionId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving messages for session [{}]: {}", sessionId, e.getMessage(), e);
            throw e;
        }
    }

}
