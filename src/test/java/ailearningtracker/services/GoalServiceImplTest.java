package ailearningtracker.services;

import ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.events.goal.SubgoalCompletedEvent;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.GoalHasAlreadyCompleted;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.MissingDataException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.GoalMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.GoalRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIService;
import com.github.mykyta.sirobaba.ailearningtracker.services.SubgoalService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private AIService aiService;
    @Mock
    private UserService userService;
    @Mock
    private GoalRepo goalRepo;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private SubgoalService subGoalService;

    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    private User testUser;
    private Goal testGoal;
    private Subgoal testSubgoal;
    private GoalRequestDto testGoalRequestDto;
    private CurrentUserInfoDto testCurrentUserInfo;

    @BeforeEach
    void setUp() {
        testUser = ModelUtils.createTestUser();
        testGoal = ModelUtils.createTestGoal();
        testSubgoal = testGoal.getSubgoals().getFirst();

        testGoalRequestDto = new GoalRequestDto();
        testGoalRequestDto.setTitle(ModelUtils.TEST_NAME_GOAL);
        testGoalRequestDto.setDeadline(ModelUtils.TEST_DEADLINE);

        testCurrentUserInfo = CurrentUserInfoDto.builder()
                .email(ModelUtils.TEST_EMAIL)
                .build();
    }

    @Test
    @DisplayName("createGoal should save goal and return GoalResponseDto")
    void createGoal_success() {
        AiPlanResponseDto aiPlanResponseDto = new AiPlanResponseDto();
        aiPlanResponseDto.setTitle(testGoal.getTitle());
        aiPlanResponseDto.setDescription(testGoal.getDescription());
        aiPlanResponseDto.setSubGoals(List.of(new SubGoalResponseDto()));

        when(aiService.createLearningPlan(testGoalRequestDto)).thenReturn(aiPlanResponseDto);
        when(goalMapper.toGoal(aiPlanResponseDto)).thenReturn(testGoal);
        when(subGoalService.createAndLinkSubGoals(aiPlanResponseDto.getSubGoals(), testGoal))
                .thenReturn(List.of(testSubgoal));
        when(userService.findByEmail(testCurrentUserInfo.getEmail())).thenReturn(testUser);
        when(goalRepo.save(testGoal)).thenReturn(testGoal);
        when(goalMapper.toGoalResponseDto(testGoal)).thenReturn(new GoalResponseDto());

        GoalResponseDto result = goalServiceImpl.createGoal(testGoalRequestDto, testCurrentUserInfo);

        assertNotNull(result);
        verify(goalRepo).save(testGoal);
        verify(subGoalService).createAndLinkSubGoals(aiPlanResponseDto.getSubGoals(), testGoal);
    }

    @Test
    @DisplayName("createGoal should throw MissingDataException when request is null")
    void createGoal_nullRequest_shouldThrowException() {
        assertThrows(MissingDataException.class, () ->
                goalServiceImpl.createGoal(null, testCurrentUserInfo)
        );
    }

    @Test
    @DisplayName("completeGoal should mark goal and subgoals as completed")
    void completeGoal_success() {
        testGoal.setCompleted(false);
        testSubgoal.setCompleted(false);

        when(goalRepo.findByGoalIdAndOwnerId(testGoal.getId(), testUser.getId()))
                .thenReturn(Optional.of(testGoal));
        when(goalRepo.save(testGoal)).thenReturn(testGoal);
        when(goalMapper.toGoalResponseDto(testGoal)).thenReturn(new GoalResponseDto());

        GoalResponseDto result = goalServiceImpl.completeGoal(testGoal.getId(), testUser.getId());

        assertNotNull(result);
        assertTrue(testGoal.isCompleted());
        assertTrue(testSubgoal.isCompleted());
        verify(goalRepo).save(testGoal);
    }

    @Test
    @DisplayName("completeGoal should throw exception if goal is already completed")
    void completeGoal_alreadyCompleted_shouldThrowException() {
        testGoal.setCompleted(true);
        when(goalRepo.findByGoalIdAndOwnerId(testGoal.getId(), testUser.getId()))
                .thenReturn(Optional.of(testGoal));

        assertThrows(GoalHasAlreadyCompleted.class,
                () -> goalServiceImpl.completeGoal(testGoal.getId(), testUser.getId())
        );
    }

    @Test
    @DisplayName("getGoal should return GoalResponseDto")
    void getGoal_success() {
        when(goalRepo.findByGoalIdAndOwnerId(testGoal.getId(), testUser.getId()))
                .thenReturn(Optional.of(testGoal));
        when(goalMapper.toGoalResponseDto(testGoal)).thenReturn(new GoalResponseDto());

        GoalResponseDto result = goalServiceImpl.getGoal(testGoal.getId(), testUser.getId());

        assertNotNull(result);
        verify(goalMapper).toGoalResponseDto(testGoal);
    }

    @Test
    @DisplayName("removeGoal should delete goal")
    void removeGoal_success() {
        when(goalRepo.findByGoalIdAndOwnerId(testGoal.getId(), testUser.getId()))
                .thenReturn(Optional.of(testGoal));

        goalServiceImpl.removeGoal(testGoal.getId(), testUser.getId());

        verify(goalRepo).delete(testGoal);
    }

    @Test
    @DisplayName("handleSubgoalCompleted should complete parent goal if all subgoals completed")
    void handleSubgoalCompleted_allSubgoalsCompleted_goalCompleted() {
        testGoal.setCompleted(false);
        testSubgoal.setCompleted(true);
        SubgoalCompletedEvent event = new SubgoalCompletedEvent(testGoal.getId());

        when(goalRepo.findById(testGoal.getId())).thenReturn(Optional.of(testGoal));
        when(goalRepo.save(testGoal)).thenReturn(testGoal);

        goalServiceImpl.handleSubgoalCompleted(event);

        assertTrue(testGoal.isCompleted());
        verify(goalRepo).save(testGoal);
    }
}
