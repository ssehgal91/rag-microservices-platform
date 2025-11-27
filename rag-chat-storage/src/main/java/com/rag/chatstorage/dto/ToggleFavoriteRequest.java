package com.rag.chatstorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "Request to mark/unmark a session as favorite")
@Data
public class ToggleFavoriteRequest {

    @NotNull(message = "Favorite flag must be provided")
    @Schema(description = "True to mark as favorite, false to unmark", example = "true")
    private boolean favorite;
}
