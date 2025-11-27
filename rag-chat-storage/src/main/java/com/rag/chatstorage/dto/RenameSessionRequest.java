package com.rag.chatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "Request to rename an existing chat session")
@Data
public class RenameSessionRequest {

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(description = "New title for the session", example = "Renamed session")
    private String title;

}
