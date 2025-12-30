package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.events.goal.SubgoalCompletedEvent;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.SubgoalHasAlreadyCompleted;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.SubgoalNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.SubgoalMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.SubgoalRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.SubgoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("Service")
@DisplayName("Subgoal service test")
@ExtendWith(MockitoExtension.class)
class SubgoalServiceImplTest {

    @Mock
    private SubgoalMapper subGoalMapper;
    @Mock
    private SubgoalRepo subGoalRepo;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SubgoalServiceImpl subgoalService;

    private Subgoal testSubgoal;
    private Goal parentGoal;

    @BeforeEach
    void setUp() {
        parentGoal = ModelUtils.createTestGoal();

        testSubgoal = ModelUtils.createTestSubgoal(parentGoal);
    }


    @Test
    @DisplayName("completeSubgoal should mark subgoal as completed, save it and publish event")
    void completeSubgoal_success() {
        SubGoalResponseDto responseDto = SubGoalResponseDto.builder()
                .id(ModelUtils.TEST_ID)
                .title(testSubgoal.getTitle())
                .description(testSubgoal.getDescription())
                .completed(true)
                .build();

        when(subGoalRepo.findBySubgoalAndUserId(1L, 2L)).thenReturn(Optional.of(testSubgoal));
        when(subGoalRepo.save(testSubgoal)).thenReturn(testSubgoal);
        when(subGoalMapper.toSubGoalResponseDto(testSubgoal)).thenReturn(responseDto);

        SubGoalResponseDto result = subgoalService.completeSubgoal(1L, 2L);

        assertNotNull(result);
        assertTrue(testSubgoal.isCompleted());
        verify(subGoalRepo).save(testSubgoal);
        verify(eventPublisher).publishEvent(any(SubgoalCompletedEvent.class));
    }

    @Test
    @DisplayName("completeSubgoal should throw SubgoalHasAlreadyCompleted when already completed")
    void completeSubgoal_alreadyCompleted_shouldThrowException() {
        testSubgoal.setCompleted(true);
        when(subGoalRepo.findBySubgoalAndUserId(1L, 2L)).thenReturn(Optional.of(testSubgoal));

        assertThrows(SubgoalHasAlreadyCompleted.class, () ->
                subgoalService.completeSubgoal(1L, 2L)
        );

        verify(subGoalRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("completeSubgoal should throw SubgoalNotFindException if subgoal not found")
    void completeSubgoal_notFound_shouldThrowException() {
        when(subGoalRepo.findBySubgoalAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(SubgoalNotFoundException.class, () ->
                subgoalService.completeSubgoal(1L, 2L)
        );

        verify(subGoalRepo, never()).save(any());
    }

    @Test
    @DisplayName("createAndLinkSubGoals should return empty list when input is null")
    void createAndLinkSubGoals_null_shouldReturnEmptyList() {
        List<Subgoal> result = subgoalService.createAndLinkSubGoals(null, parentGoal);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("createAndLinkSubGoals should return empty list when input is empty")
    void createAndLinkSubGoals_empty_shouldReturnEmptyList() {
        List<Subgoal> result = subgoalService.createAndLinkSubGoals(Collections.emptyList(), parentGoal);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("createAndLinkSubGoals should map DTOs to entities and set parent goal")
    void createAndLinkSubGoals_shouldMapCorrectly() {
        SubGoalResponseDto dto = SubGoalResponseDto.builder()
                .title("Learn Vocabulary")
                .description("Memorize 50 words")
                .estimatedHours(5)
                .completed(false)
                .build();

        Subgoal mapped = Subgoal.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .estimatedHours(dto.getEstimatedHours())
                .build();

        when(subGoalMapper.toSubGoal(dto)).thenReturn(mapped);

        List<Subgoal> result = subgoalService.createAndLinkSubGoals(List.of(dto), parentGoal);

        assertEquals(1, result.size());
        assertEquals(parentGoal, result.getFirst().getGoal());
        verify(subGoalMapper).toSubGoal(dto);
    }
}
