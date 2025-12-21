package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when attempting to modify or update a goal
 * that has already been marked as completed.
 * <p>
 * Protects completed goals from further changes.
 *
 * Typically results in HTTP 400 (Bad Request).
 */
public class GoalHasAlreadyCompleted extends RuntimeException {
    public GoalHasAlreadyCompleted(String message) {
        super(message);
    }
}

