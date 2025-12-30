package com.github.mykyta.sirobaba.ailearningtracker;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.*;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
public class ModelUtils {
    public static final String TEST_NAME_GOAL = "English";
    public static final String TEST_DESCRIPTION_GOAL = "I want to learn English";
    public static final LocalDate TEST_DEADLINE = LocalDate.of(2026, 5, 20);
    public static final Difficulty TEST_DIFFICULTY = Difficulty.MEDIUM;
    public static final LocalDateTime TEST_LOG_TIME = LocalDateTime.of(2025, 11, 18, 10, 30);
    public static final String TEST_NAME_SUBGOAL = "English";
    public static final String TEST_DESCRIPTION_SUBGOAL = "You should revise all the grammar rules";
    public static final Integer TEST_ESTIMATED_HOURS = 20;
    public static final Long TEST_ID = 1L;
    public static final String TEST_EMAIL = "work@gmail.com";
    public static final String TEST_PASSWORD = "password";
    public static final String TEST_TITLE_PROGRESS_LOG = "Check my progress";
    public static final String TEST_PROGRESS_LOG_NOTE = "I practice communication a lot";
    public static final Integer TEST_MINUTES_SPENT_ON_TASK = 135;
    public static final String TEST_AI_ANALYSIS_TITLE = "English";
    public static final String TEST_ANALYSIS_TEXT = "You have done a really great job," +
                                                    " but you should pay more attention to grammar.";
    public static final LocalDateTime TEST_CREATED_AT = LocalDateTime.of(2026, 5, 21, 10, 30);


    public static User createTestUser() {
        return User.builder()
                .id(TEST_ID)
                .password(TEST_PASSWORD)
                .email(TEST_EMAIL)
                .goals(List.of())
                .build();
    }

    public static Goal createTestGoal() {
        Goal goal = Goal.builder()
                .id(TEST_ID)
                .title(TEST_NAME_GOAL)
                .description(TEST_DESCRIPTION_GOAL)
                .createdAt(LocalDate.now())
                .difficulty(TEST_DIFFICULTY)
                .deadline(TEST_DEADLINE)
                .estimatedHours(TEST_ESTIMATED_HOURS)
                .aiAnalyses(List.of())
                .user(createTestUser())
                .build();

        Subgoal subgoal = createTestSubgoal(goal);
        goal.setSubgoals(List.of(subgoal));

        return goal;
    }

    public static Subgoal createTestSubgoal(Goal parentGoal) {
        return Subgoal.builder()
                .id(TEST_ID)
                .title(TEST_NAME_SUBGOAL)
                .description(TEST_DESCRIPTION_SUBGOAL)
                .difficulty(Difficulty.MEDIUM)
                .estimatedHours(TEST_ESTIMATED_HOURS)
                .goal(parentGoal)
                .build();
    }


    public static ProgressLog createTestProgressLog() {
        return ProgressLog.builder()
                .id(TEST_ID)
                .title(TEST_TITLE_PROGRESS_LOG)
                .note(TEST_PROGRESS_LOG_NOTE)
                .logTime(TEST_LOG_TIME)
                .minutesSpent(TEST_MINUTES_SPENT_ON_TASK)
                .goal(createTestGoal())
                .build();
    }

    public static AIAnalysis createTestAIAnalysis() {
        return AIAnalysis.builder()
                .id(TEST_ID)
                .title(TEST_AI_ANALYSIS_TITLE)
                .goal(createTestGoal())
                .createdAt(TEST_CREATED_AT)
                .analysisText(TEST_ANALYSIS_TEXT)
                .build();
    }

}
