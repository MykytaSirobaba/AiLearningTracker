package com.github.mykyta.sirobaba.ailearningtracker.mappers;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.SubgoalMapper;
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
@DisplayName("SubgoalMapper Tests")
class SubgoalMapperTest {

    private final SubgoalMapper subgoalMapper = Mappers.getMapper(SubgoalMapper.class);

    private final Goal goal = Goal.builder().id(1L).build();
    private final Subgoal subgoal = ModelUtils.createTestSubgoal(goal);

    @Nested
    @DisplayName("Method: toSubGoalResponseDto")
    class ToSubGoalResponseDtoTests {

        @Test
        @DisplayName("Should map all fields correctly from Subgoal to DTO")
        void toSubGoalResponseDto_ShouldMapAllFieldsCorrectly() {
            SubGoalResponseDto dto = subgoalMapper.toSubGoalResponseDto(subgoal);

            assertNotNull(dto, "The resulting DTO should not be null");
            assertEquals(subgoal.getId(), dto.getId(), "ID must be mapped correctly");
            assertEquals(TEST_NAME_SUBGOAL, dto.getTitle(), "Title must be mapped correctly");
            assertEquals(TEST_DESCRIPTION_SUBGOAL, dto.getDescription(), "Description must be mapped correctly");
            assertFalse(dto.isCompleted(), "Completed status must be mapped correctly");
        }

        @Test
        @DisplayName("Should return null when the input Subgoal is null")
        void toSubGoalResponseDto_ShouldReturnNullWhenInputIsNull() {
            assertNull(subgoalMapper.toSubGoalResponseDto(null));
        }
    }

    @Nested
    @DisplayName("Method: toSubGoal (DTO to Subgoal)")
    class ToSubGoalTests {

        @Test
        @DisplayName("Should apply constant and ignore mappings correctly")
        void toSubGoal_ShouldApplyMappingsCorrectly() {
            SubGoalResponseDto dto = new SubGoalResponseDto();
            dto.setId(TEST_ID);
            dto.setTitle(TEST_NAME_SUBGOAL);
            dto.setDescription(TEST_DESCRIPTION_SUBGOAL);
            dto.setCompleted(true);

            Subgoal subGoal = subgoalMapper.toSubGoal(dto);

            assertNotNull(subGoal, "The resulting Subgoal should not be null");

            assertEquals(TEST_ID, subGoal.getId(), "ID must be mapped correctly");
            assertEquals(TEST_NAME_SUBGOAL, subGoal.getTitle(), "Title must be mapped correctly");
            assertEquals(TEST_DESCRIPTION_SUBGOAL, subGoal.getDescription(), "Description must be mapped correctly");

            assertFalse(subGoal.isCompleted(), "Completed must be forcibly set to false, ignoring DTO value");
            assertNull(subGoal.getGoal(), "Goal must be null because it is ignored");
        }

        @Test
        @DisplayName("Should return null when the input DTO is null")
        void toSubGoal_ShouldReturnNullWhenInputIsNull() {
            assertNull(subgoalMapper.toSubGoal(null));
        }
    }
}