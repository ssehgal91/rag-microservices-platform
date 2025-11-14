package com.rag.chatstorage.exception;

/**
 * Thrown when a chat session with the specified ID is not found in the system.
 *
 * This is a domain-specific exception that translates to a 404 Not Found response
 * via the {@link GlobalExceptionHandler}.
 */
public class ChatSessionNotFoundException extends RuntimeException {

    public ChatSessionNotFoundException(String message) {
        super(message);
    }

    public ChatSessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
