package com.github.mykyta.sirobaba.ailearningtracker.mappers;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.GoalMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.GoalMapperImpl;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.SubgoalMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static com.github.mykyta.sirobaba.ailearningtracker.ModelUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@DisplayName("GoalMapper Tests")
class GoalMapperTest {

    private final SubgoalMapper subgoalMapper = Mappers.getMapper(SubgoalMapper.class);
    private final GoalMapper goalMapper = new GoalMapperImpl(subgoalMapper);

    private final Goal testGoal = ModelUtils.createTestGoal();
    private AiPlanResponseDto testAiPlanResponseDto;

    @BeforeEach
    void setUp() {
        testAiPlanResponseDto = new AiPlanResponseDto();
        testAiPlanResponseDto.setTitle(TEST_NAME_GOAL);
        testAiPlanResponseDto.setDescription(TEST_DESCRIPTION_GOAL);
        testAiPlanResponseDto.setDifficulty(TEST_DIFFICULTY);
        testAiPlanResponseDto.setDeadline(TEST_DEADLINE);
    }

    @Nested
    @DisplayName("Method: toGoal (AiPlanResponseDto to Goal)")
    class ToGoalTests {

        @Test
        @DisplayName("Should map fields and apply constant/ignore rules correctly")
        void toGoal_ShouldMapAndApplyRulesCorrectly() {
            Goal goal = goalMapper.toGoal(testAiPlanResponseDto);

            assertNotNull(goal);

            assertEquals(TEST_NAME_GOAL, goal.getTitle(), "Title should be mapped");
            assertEquals(TEST_DESCRIPTION_GOAL, goal.getDescription(), "Description should be mapped");
            assertEquals(TEST_DIFFICULTY, goal.getDifficulty(), "Difficulty should be mapped");

            assertFalse(goal.isCompleted(), "Completed must be forcibly set to false");
            assertNull(goal.getId(), "ID must be null because it is ignored");
            assertNull(goal.getSubgoals(), "Subgoals must be null because it is ignored");
        }

        @Test
        @DisplayName("Should return null when input DTO is null")
        void toGoal_ShouldReturnNullWhenInputIsNull() {
            assertNull(goalMapper.toGoal(null));
        }

        @Test
        @DisplayName("Should handle null deadline string")
        void toGoal_ShouldHandleNullDeadline() {
            testAiPlanResponseDto.setDeadline(null);
            Goal goal = goalMapper.toGoal(testAiPlanResponseDto);
            assertNull(goal.getDeadline());
        }
    }

    @Nested
    @DisplayName("Method: toGoalResponseDto (Goal to DTO)")
    class ToGoalResponseDtoTests {

        @Test
        @DisplayName("Should map all fields correctly from Goal to DTO")
        void toGoalResponseDto_ShouldMapAllFieldsCorrectly() {
            GoalResponseDto dto = goalMapper.toGoalResponseDto(testGoal);

            assertNotNull(dto);

            assertEquals(TEST_ID, dto.getId(), "ID must be mapped");
            assertEquals(TEST_NAME_GOAL, dto.getTitle(), "Title must be mapped");
            assertEquals(TEST_DESCRIPTION_GOAL, dto.getDescription(), "Description must be mapped");
            assertEquals(TEST_DIFFICULTY, dto.getDifficulty(), "Difficulty must be mapped");
            assertEquals(TEST_DEADLINE, dto.getDeadline(), "Deadline must be mapped");
            assertEquals(TEST_ESTIMATED_HOURS, dto.getEstimatedHours(), "EstimatedHours must be mapped");
            assertFalse(dto.isCompleted(), "Completed status must be mapped");

            assertNotNull(dto.getSubgoals(), "Subgoals list should be mapped/initialized");
        }

        @Test
        @DisplayName("Should return null when input Goal is null")
        void toGoalResponseDto_ShouldReturnNullWhenInputIsNull() {
            assertNull(goalMapper.toGoalResponseDto(null));
        }
    }
}