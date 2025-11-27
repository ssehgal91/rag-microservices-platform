package com.rag.chatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Request to create a new chat session")
@Data
public class CreateSessionRequest {
    @NotBlank(message = "User ID cannot be blank")
    @Schema(description = "Unique ID of the user creating the session", example = "u1")
    private String userId;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(description = "Title of the chat session", example = "Demo conversation")
    private String title;

}
