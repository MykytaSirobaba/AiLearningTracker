package com.github.mykyta.sirobaba.ailearningtracker.constant;

import lombok.experimental.UtilityClass;

/**
 * Created by Mykyta Sirobaba on 16.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@UtilityClass
public final class ErrorMessage {
    public static final String USER_WITH_THIS_EMAIL_NOT_FOUND = "User with the email %s does not exist";
    public static final String GOAL_REQUESTED_IS_MISSING = "goalRequestDto is missing";
    public static final String AI_JSON_PARSE = "Failed to parse AI JSON: %s";
    public static final String GOAL_NOT_FOUND = "Goal with id %s not found";
    public static final String UNREALISTIC_DEADLINE = "Unrealistic deadline. Hours needed to complete: %d. You only have %s hours (based on %d hours/week until %s).";
    public static final String AI_RETURNED_NOT_PARSABLE_CONTENT = "AI returned non-parsable content (likely code block without valid JSON): %s";
    public static final String AI_NOT_RETURN_JSON = "AI did not return JSON: %s";
    public static final String EMAIL_ALREADY_REGISTERED = "Email already in use";

}
