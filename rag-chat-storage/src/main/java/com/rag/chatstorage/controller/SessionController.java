package com.rag.chatstorage.controller;

import com.rag.chatstorage.dto.CreateSessionRequest;
import com.rag.chatstorage.dto.RenameSessionRequest;
import com.rag.chatstorage.dto.SessionResponse;
import com.rag.chatstorage.dto.ToggleFavoriteRequest;
import com.rag.chatstorage.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing chat sessions.
 *
 * @author Sakshi Sehgal
 */
@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
@Tag(name = "Session Management", description = "APIs for managing user chat sessions")
public class SessionController {

    private final SessionService sessionService;

    /**
     * Creates a new chat session for a user.
     *
     * @param request The session creation request containing userId and title.
     * @return A {@link SessionResponse} representing the created session.
     */
    @Operation(
            summary = "Create a new chat session",
            description = "Creates a new chat session for a given user ID with the provided title.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Session created successfully",
                            content = @Content(schema = @Schema(implementation = SessionResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@RequestBody @Valid CreateSessionRequest request) {
        SessionResponse sessionResponse = sessionService.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionResponse);
    }

    /**
     * Retrieves all chat sessions for a specific user.
     *
     * @param userId ID of the user whose sessions are to be fetched.
     * @return list of {@link SessionResponse}.
     */
    @GetMapping("/user/{userId}")
    @Operation(
            summary = "List sessions for a user",
            description = "Fetches all chat sessions associated with the specified user ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SessionResponse.class)))
            }
    )
    public ResponseEntity<List<SessionResponse>> getSessionsForUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {

        List<SessionResponse> sessions = sessionService.getSessionsForUser(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * Retrieves all chat sessions across all users.
     *
     * @return list of {@link SessionResponse}.
     */
    @Operation(
            summary = "Get all sessions (admin)",
            description = "Fetches all chat sessions across all users. Useful for admin or internal use.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "All sessions retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SessionResponse.class)))
            }
    )
    @GetMapping("/all")
    public ResponseEntity<List<SessionResponse>> getAllSessions() {
        List<SessionResponse> sessions = sessionService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Retrieves a single session by its ID.
     *
     * @param sessionId ID of the session to retrieve.
     * @return the {@link SessionResponse}
     */
    @Operation(
            summary = "Get session by Session ID",
            description = "Fetches a single chat session using its unique ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session retrieved successfully",
                            content = @Content(schema = @Schema(implementation = SessionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
            }
    )
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSessionById(
            @Parameter(description = "Session ID", required = true)
            @PathVariable UUID sessionId) {

        SessionResponse response = sessionService.getSessionById(sessionId);
        return ResponseEntity.ok(response);
    }


    /**
     * Renames an existing chat session.
     *
     * @param sessionId     the UUID of the session to rename.
     * @param request the {@link RenameSessionRequest} containing new title.
     * @return the updated {@link SessionResponse}.
     */
    @Operation(
            summary = "Rename a chat session",
            description = "Updates the title of an existing chat session.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Session renamed successfully",
                            content = @Content(schema = @Schema(implementation = SessionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
            }
    )
    @PatchMapping("/{sessionId}/rename")
    public ResponseEntity<SessionResponse> renameSession(
            @Parameter(description = "Session ID", required = true) @PathVariable UUID sessionId,
            @Valid @RequestBody RenameSessionRequest request) {

        SessionResponse updated = sessionService.renameSession(sessionId, request);
        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Mark or unmark a session as favorite",
            description = "Toggles the favorite status of a session.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Favorite status updated successfully",
                            content = @Content(schema = @Schema(implementation = SessionResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
            }
    )
    @PatchMapping("/{id}/favorite")
    public ResponseEntity<SessionResponse> favorite(
            @Parameter(description = "Session ID", required = true) @PathVariable UUID id,
            @Valid @RequestBody ToggleFavoriteRequest request) {

        SessionResponse updated = sessionService.toggleFavorite(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a chat session by its ID.
     *
     * @param sessionId the UUID of the session to delete.
     * @return HTTP 204 No Content.
     */
    @Operation(
            summary = "Delete a chat session",
            description = "Deletes an existing chat session and all its messages.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
            }
    )
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "Session ID", required = true) @PathVariable UUID sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
