package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when a progress log entry cannot be found
 * for the specified user or goal.
 * <p>
 * Protects user data by ensuring access only to owned logs.
 *
 * Typically results in HTTP 404 (Not Found).
 */
public class ProgressLogNotFoundException extends RuntimeException {
    public ProgressLogNotFoundException(String message) {
        super(message);
    }
}

