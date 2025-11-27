package com.rag.chatstorage.service;

import com.rag.chatstorage.dto.MessageRequest;
import com.rag.chatstorage.dto.MessageResponse;
import com.rag.chatstorage.entity.Message;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface MessageService {
    MessageResponse addMessage(UUID sessionId, MessageRequest request);
    Page<MessageResponse> getMessagesBySessionId(UUID sessionId, int page, int size);
}
