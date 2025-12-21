package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when a user with the specified email does not exist.
 * <p>
 * Used during login, password reset, or email-based lookups.
 *
 * Typically results in HTTP 404 (Not Found).
 */
public class UserEmailNotFoundException extends RuntimeException {
    public UserEmailNotFoundException(String message) {
        super(message);
    }
}
