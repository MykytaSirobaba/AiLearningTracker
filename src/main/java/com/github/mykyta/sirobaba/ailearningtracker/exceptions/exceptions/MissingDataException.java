package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when required data is missing from a request or the database.
 * <p>
 * Used for validation of incomplete user input or inconsistent stored data.
 *
 * Typically results in HTTP 400 (Bad Request).
 */
public class MissingDataException extends RuntimeException {
    public MissingDataException(String message) {
        super(message);
    }
}

