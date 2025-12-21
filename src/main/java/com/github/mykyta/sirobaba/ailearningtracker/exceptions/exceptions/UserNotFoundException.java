package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when a user with the specified ID cannot be found.
 * <p>
 * Used in profile operations, authentication, and resource ownership validation.
 *
 * Typically results in HTTP 404 (Not Found).
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

