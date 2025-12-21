package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when the AI model returns a response that cannot be parsed into
 * the expected JSON structure.
 * <p>
 * This error occurs when the AI produces invalid JSON, missing fields,
 * or content wrapped in non-JSON formatting.
 *
 * Typically results in HTTP 500 (Internal Server Error).
 */
public class AiJsonParseException extends RuntimeException {
    public AiJsonParseException(String message) {
        super(message);
    }
}
