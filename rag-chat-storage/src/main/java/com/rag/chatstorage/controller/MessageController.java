package com.rag.chatstorage.controller;

import com.rag.chatstorage.dto.MessageRequest;
import com.rag.chatstorage.dto.MessageResponse;
import com.rag.chatstorage.entity.Message;
import com.rag.chatstorage.service.MessageService;
import com.rag.chatstorage.service.impl.MessageServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controller for managing chat messages within chat sessions.
 *
 * @author Sakshi Sehgal
 */
@RestController
@RequestMapping("/api/v1/sessions/{sessionId}/messages")
@RequiredArgsConstructor
@Tag(name = "Chat Messages", description = "APIs for managing chat messages")
public class MessageController {

    private final MessageService messageService;

    /**
     * Adds a new chat message to a session.
     *
     * @param sessionId ID of the session.
     * @param request   The {@link MessageRequest} containing message details.
     * @return The created {@link MessageResponse}.
     */
    @Operation(
            summary = "Add a new message to a session",
            description = "Adds a message sent by a user within a chat session.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Message creation request",
                    content = @Content(
                            schema = @Schema(implementation = MessageRequest.class),
                            examples = @ExampleObject(
                                    name = "New message",
                                    value = "{ \"sender\": \"user123\", \"content\": \"Hello there!\", \"context\": \"greeting\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Message created successfully",
                            content = @Content(schema = @Schema(implementation = MessageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Session not found", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<MessageResponse> addMessage(
            @Parameter(description = "Session ID to which the message belongs", required = true)
            @PathVariable UUID sessionId,
            @Valid @RequestBody MessageRequest request) {

        MessageResponse response = messageService.addMessage(sessionId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a paginated list of messages for a session.
     *
     * @param sessionId the session ID whose messages should be retrieved.
     * @param page page number (0-indexed)
     * @param size number of records per page
     * @return a paginated list of {@link MessageResponse}.
     */
    @Operation(
            summary = "List messages for a session",
            description = "Fetches a paginated list of chat messages for the given session ID.",
            security = {@SecurityRequirement(name = "ApiKeyAuth")},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Messages retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = MessageResponse.class)
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Session not found",
                            content = @Content
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<MessageResponse>> listMessages(
            @Parameter(description = "Session ID", required = true)
            @PathVariable UUID sessionId,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of messages per page")
            @RequestParam(defaultValue = "20") int size) {

        Page<MessageResponse> messages =
                messageService.getMessagesBySessionId(sessionId, page, size);

        return ResponseEntity.ok(messages);
    }

}
