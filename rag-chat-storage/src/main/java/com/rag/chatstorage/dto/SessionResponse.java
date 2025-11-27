package com.rag.chatstorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Response model representing a chat session returned to API clients.
 */
@Schema(description = "Response object representing a chat session")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class SessionResponse {

    @Schema(description = "Unique identifier of the session", example = "b1f7a4d2-9f22-4e19-bb3c-ff3df1c72b1a")
    private UUID id;

    @Schema(description = "User ID associated with the session", example = "u1")
    private String userId;

    @Schema(description = "Title or name of the chat session", example = "My first chat session")
    private String title;

    @Schema(description = "Whether this session is marked as favorite", example = "false")
    private boolean favorite;

    @Schema(description = "Timestamp when the session was created", example = "2025-11-12T13:45:00Z")
    private OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the session was last updated", example = "2025-11-12T14:10:32Z")
    private OffsetDateTime updatedAt;
}
