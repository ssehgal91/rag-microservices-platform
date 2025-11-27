package com.rag.chatstorage.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Unified error response model for all API exceptions.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private OffsetDateTime timestamp;
    private Map<String, String> details;
}