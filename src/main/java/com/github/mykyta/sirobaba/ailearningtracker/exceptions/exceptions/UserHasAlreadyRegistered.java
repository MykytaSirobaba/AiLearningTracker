package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when attempting to register a new user with an email or username
 * that already exists in the system.
 * <p>
 * Prevents duplicate account creation.
 *
 * Typically results in HTTP 409 (Conflict).
 */
public class UserHasAlreadyRegistered extends RuntimeException {
    public UserHasAlreadyRegistered(String message) {
        super(message);
    }
}
