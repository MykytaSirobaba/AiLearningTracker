package com.github.mykyta.sirobaba.ailearningtracker.exceptions.handler;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.ExceptionResponse;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

/**
 * Global exception handler that centralizes error processing across the application.
 * <p>
 * This class intercepts and transforms exceptions into consistent and structured
 * {@link ExceptionResponse} objects, ensuring standardized error output for all controllers.
 * </p>
 *
 * <p><strong>Responsibilities:</strong></p>
 * <ul>
 *     <li>Handle domain-specific exceptions such as missing resources or validation errors.</li>
 *     <li>Map different exception types to appropriate HTTP status codes.</li>
 *     <li>Provide fallback handling for unexpected server errors.</li>
 *     <li>Attach request information (URI, time, message) to error responses.</li>
 * </ul>
 */
@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * Handles cases when a requested resource cannot be found in the system.
     * <p>
     * This includes missing goals, subgoals, users, progress logs, and AI analyses.
     * </p>
     *
     * @param ex      the thrown {@link RuntimeException}
     * @param request the current web request context
     * @return response with HTTP 404 Not Found and structured error details
     */
    @ExceptionHandler({
            GoalNotFoundException.class,
            SubgoalNotFoundException.class,
            UserEmailNotFoundException.class,
            UserNotFoundException.class,
            ProgressLogNotFoundException.class,
            AiAnalysisInThisGoalNotFound.class
    })
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(RuntimeException ex, WebRequest request) {
        log.debug("Resource not found: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Handles situations when a request conflicts with the current application state.
     * <p>
     * Typical examples include attempts to complete an already completed goal
     * or register a user who already exists.
     * </p>
     *
     * @param ex      the thrown {@link RuntimeException}
     * @param request the current web request context
     * @return response with HTTP 409 Conflict and error details
     */
    @ExceptionHandler({
            GoalHasAlreadyCompleted.class,
            SubgoalHasAlreadyCompleted.class,
            UserHasAlreadyRegistered.class
    })
    public ResponseEntity<ExceptionResponse> handleConflictExceptions(RuntimeException ex, WebRequest request) {
        log.debug("State conflict: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Handles client-side errors caused by invalid or incomplete request data.
     * <p>
     * This covers validation failures, malformed JSON, or missing mandatory fields.
     * </p>
     *
     * @param ex      the thrown {@link RuntimeException}
     * @param request the current web request context
     * @return response with HTTP 400 Bad Request and error details
     */
    @ExceptionHandler({
            GoalValidationException.class,
            AiJsonParseException.class,
            MissingDataException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestExceptions(RuntimeException ex, WebRequest request) {
        log.debug("Incorrect request: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Fallback handler for unexpected or unhandled server-side errors.
     * <p>
     * Logs the full stack trace to help diagnose internal failures and returns a generic
     * server error response without exposing sensitive details.
     * </p>
     *
     * @param ex      any unhandled {@link Exception}
     * @param request the current web request context
     * @return response with HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected server error: {}", ex.getMessage(), ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.UNEXPECTED_SERVER_ERROR, request);
    }

    @ExceptionHandler({
            InvalidRefreshTokenException.class,
            Invalid2FaTokenException.class
    })
    public ResponseEntity<ExceptionResponse> handleInvalidToken(RuntimeException ex, WebRequest request) {
        log.debug("Invalid token: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    /**
     * Builds a standardized {@link ExceptionResponse} for API error output.
     *
     * @param status  HTTP status to return
     * @param message message describing the error
     * @param request the current web request context
     * @return an HTTP response containing the structured error body
     */
    private ResponseEntity<ExceptionResponse> createErrorResponse(
            HttpStatus status,
            String message,
            WebRequest request
    ) {
        ExceptionResponse error = new ExceptionResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                getRequestUri(request)
        );
        return new ResponseEntity<>(error, status);
    }

    /**
     * Extracts the request URI from the {@link WebRequest}.
     *
     * @param request the current request
     * @return the URI of the request
     */
    private String getRequestUri(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return request.getDescription(false);
    }
}
