package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when the requested subgoal cannot be found
 * within the user's goal.
 * <p>
 * Ensures correct ownership and scoping of subgoals.
 *
 * Typically results in HTTP 404 (Not Found).
 */
public class SubgoalNotFoundException extends RuntimeException {
    public SubgoalNotFoundException(String message) {
        super(message);
    }
}

