package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when a goal with the specified ID cannot be found
 * for the current user.
 * <p>
 * Ensures that users can access only their own goals.
 *
 * Typically results in HTTP 404 (Not Found).
 */
public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String message) {
        super(message);
    }
}

