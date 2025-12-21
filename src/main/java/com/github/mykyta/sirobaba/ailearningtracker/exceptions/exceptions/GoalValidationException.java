package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when goal creation or update violates business validation rules.
 * <p>
 * Most commonly used for unrealistic deadlines, invalid estimated hours,
 * or inconsistencies between subgoals and the main goal's plan.
 *
 * Typically results in HTTP 400 (Bad Request).
 */
public class GoalValidationException extends RuntimeException {
    public GoalValidationException(String message) {
        super(message);
    }
}
