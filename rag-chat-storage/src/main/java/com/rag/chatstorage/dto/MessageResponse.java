package com.rag.chatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO representing a chat message in API responses.
 *
 * This object is returned when a message is created or retrieved from a session.
 * It mirrors the essential information from the {@link com.rag.chatstorage.entity.Message} entity,
 * excluding persistence details and relationships.
 */
@Schema(description = "Response payload representing a chat message")
@Data
public class MessageResponse {

    @Schema(
            description = "Unique identifier of the message",
            example = "4b2d1a1e-82cf-4f87-9f1f-9a3d8e6d52a2"
    )
    private UUID id;

    @Schema(
            description = "Unique identifier of the session this message belongs to",
            example = "c5e0f2c3-dfe1-4c51-9363-345ee35c87ab"
    )
    private UUID sessionId;

    @Schema(
            description = "Sender of the message (e.g., user ID or 'assistant')",
            example = "user123"
    )
    private String sender;

    @Schema(
            description = "Content of the message",
            example = "Hi there! How can I help you today?"
    )
    private String content;

    @Schema(
            description = "Optional JSON context associated with the message (AI metadata, retrieval info, etc.)",
            example = "{\"intent\": \"greeting\", \"confidence\": 0.95}"
    )
    private String context;

    @Schema(
            description = "Timestamp when the message was created (UTC ISO 8601 format)",
            example = "2025-11-12T21:15:35.120Z"
    )
    private OffsetDateTime createdAt;
}
