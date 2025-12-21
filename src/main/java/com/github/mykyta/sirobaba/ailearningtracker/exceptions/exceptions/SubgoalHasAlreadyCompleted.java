package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when attempting to modify a subgoal
 * that has already been marked as completed.
 * <p>
 * Prevents double completion and maintains a consistent progress state.
 *
 * Typically results in HTTP 400 (Bad Request).
 */
public class SubgoalHasAlreadyCompleted extends RuntimeException {
    public SubgoalHasAlreadyCompleted(String message) {
        super(message);
    }
}
