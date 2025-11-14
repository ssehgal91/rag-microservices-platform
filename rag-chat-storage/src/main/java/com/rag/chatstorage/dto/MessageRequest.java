package com.rag.chatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO representing a request to create a new chat message.
 *
 */
@Schema(description = "Request payload for creating a new chat message")
@Data
public class MessageRequest {

    @Schema(
            description = "Sender of the message (e.g., 'user', 'assistant', or user ID)",
            example = "user123",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Sender is required")
   /* NotBalnk--Only String fields
    Value must not be null, empty, or whitespace-only
null, "", " "*/
    private String sender;

    @Schema(
            description = "Message text content",
            example = "Hi, how can I help you today?",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Message content cannot be blank")
    private String content;

    @Schema(
            description = "Optional context metadata (e.g., AI context or retrieval info in JSON format)",
            example = "{\"intent\": \"greeting\"}"
    )
    private String context;
}
