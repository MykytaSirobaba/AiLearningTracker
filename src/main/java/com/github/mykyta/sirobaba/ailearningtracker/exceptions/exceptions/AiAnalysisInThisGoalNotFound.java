package com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions;

/**
 * Exception thrown when an AI analysis with the specified ID does not belong to the given goal
 * or cannot be found under that goal.
 * <p>
 * This exception indicates that the user is trying to access an AI analysis that does not exist
 * within the context of the specified goal, ensuring strict resource scoping.
 *
 * Typically results in HTTP 404 (Not Found).
 */
public class AiAnalysisInThisGoalNotFound extends RuntimeException {
    public AiAnalysisInThisGoalNotFound(String message) {
        super(message);
    }
}

