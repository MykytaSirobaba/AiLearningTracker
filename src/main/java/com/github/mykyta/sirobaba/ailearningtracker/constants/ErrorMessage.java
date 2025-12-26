package com.github.mykyta.sirobaba.ailearningtracker.constants;

import lombok.experimental.UtilityClass;

/**
 * Created by Mykyta Sirobaba on 16.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@UtilityClass
public final class ErrorMessage {
    public static final String USER_WITH_THIS_EMAIL_NOT_FOUND = "User with the email %s does not exist";
    public static final String USER_WITH_THIS_ID_NOT_FOUND = "User with id: %d not found";
    public static final String GOAL_REQUESTED_IS_MISSING = "goalRequestDto is missing";
    public static final String AI_JSON_PARSE = "Failed to parse AI JSON: %s";
    public static final String GOAL_NOT_FOUND = "Goal with id %s not found";
    public static final String GOAL_WITH_THIS_OWNER_NOT_FOUND = "Goal with ID %d not found for user %d";
    public static final String UNREALISTIC_DEADLINE = "Unrealistic deadline. Hours needed to complete: %d. You only have %s hours (based on %d hours/week until %s).";
    public static final String AI_RETURNED_NOT_PARSABLE_CONTENT = "AI returned non-parsable content (likely code block without valid JSON): %s";
    public static final String AI_NOT_RETURN_JSON = "AI did not return JSON: %s";
    public static final String EMAIL_ALREADY_REGISTERED = "Email already in use";
    public static final String GOAL_COMPLETED = "Goal has already been completed id: %d";
    public static final String SUBGOAL_NOT_FOUND = "Subgoal with id %s not found";
    public static final String SUBGOAL_WITH_THIS_OWNER_NOT_FOUND = "Subgoal with ID %d not found for user %d";
    public static final String SUBGOAL_COMPLETED = "Subgoal has been completed id: %d";
    public static final String UNEXPECTED_SERVER_ERROR = "An unexpected server error has occurred.";
    public static final String PROGRESS_LOGS_NOT_FOUND = "Progress logs not found id: %d";
    public static final String PROGRESS_LOGS_CONTENT_NOT_FOUND = "No progress logs found for goalId: %d, userId: %d";
    public static final String AI_ANALYSIS_IN_THIS_GOAL_NOT_FOUND = "AI analysis with id=%d not found in goal with id=%d";
    public static final String USER_ALREADY_REGISTERED_WITH_THIS_NAME = "User has already registered with this name";
    public static final String INVALID_TOKEN_TYPE = "Invalid token type";
    public static final String USER_NOT_FOUND_FROM_2FA_TOKEN = "User not found from 2FA token";
    public static final String INVALID_2FA_CODE = "Invalid 2FA code";
    public static final String USER_NOT_FOUND_AFTER_OAUT2 = "User not found after OAuth2 login";
    public static final String USER_DID_NOT_COMPLETE_SETUP_STAGE = "The user did not complete the setup stage";
    public static final String TWO_FACTOR_ALREADY_ENABLED = "Two factor already enabled";
    public static final String TIME_FOR_ACTIVATION_EXPIRED = "Activation time has expired";
    public static final String TWO_FACTOR_NOT_ENABLED = "Two factor is not enabled";
    public static final String USER_WITH_THIS_USERNAME_NOT_FOUND = "User with the username %s not found";
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token: %s";
    public static final String AI_RETURNED_EMPTY_RESPONSE = "AI returned empty response: %s";
}
