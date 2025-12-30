package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.events.goal.SubgoalCompletedEvent;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.GoalHasAlreadyCompleted;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.GoalNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.MissingDataException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.GoalMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.GoalRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIService;
import com.github.mykyta.sirobaba.ailearningtracker.services.GoalService;
import com.github.mykyta.sirobaba.ailearningtracker.services.SubgoalService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service implementation for managing learning goals.
 * <p>
 * Handles creation, completion, retrieval, deletion, and pagination of goals.
 * Integrates with AIService for generating study plans and SubgoalService for managing subgoals.
 */
@Slf4j
@Service
@AllArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final AIService aiService;
    private final UserService userService;
    private final GoalRepo goalRepo;
    private final GoalMapper goalMapper;
    private final SubgoalService subGoalService;

    /**
     * Creates a new learning goal for the current user.
     * <p>
     * Generates an AI-based learning plan, creates related subgoals,
     * calculates estimated total hours, and persists the goal.
     *
     * @param goalRequestDto DTO containing goal description and preferences
     * @param user           current authenticated user
     * @return response DTO representing the created goal
     * @throws MissingDataException if request data or user data is missing
     */
    @Override
    @Transactional
    public GoalResponseDto createGoal(GoalRequestDto goalRequestDto, CurrentUserInfoDto user) {
        log.info("Creating goal for user: email={}", user.getEmail());

        if (goalRequestDto == null || userService.findByEmail(user.getEmail()).getEmail() == null) {
            log.warn("Missing goal request data or user data for email={}", user.getEmail());
            throw new MissingDataException(ErrorMessage.GOAL_REQUESTED_IS_MISSING);
        }

        AiPlanResponseDto aiPlanResponseDto = aiService.createLearningPlan(goalRequestDto);
        Goal newGoal = goalMapper.toGoal(aiPlanResponseDto);

        List<Subgoal> subGoals = subGoalService.createAndLinkSubGoals(
                aiPlanResponseDto.getSubGoals(),
                newGoal
        );

        newGoal.setSubgoals(subGoals);
        newGoal.setEstimatedHours(
                subGoals.stream()
                        .map(Subgoal::getEstimatedHours)
                        .reduce(0, Integer::sum)
        );
        newGoal.setCreatedAt(LocalDate.now());
        newGoal.setUser(userService.findByEmail(user.getEmail()));

        Goal savedGoal = goalRepo.save(newGoal);
        log.info("Goal created successfully: id={}, userEmail={}", savedGoal.getId(), user.getEmail());
        return goalMapper.toGoalResponseDto(savedGoal);
    }

    /**
     * Marks a goal as completed for the specified user.
     * <p>
     * Also marks all related subgoals as completed if they are not already.
     *
     * @param id     goal identifier
     * @param userId identifier of the goal owner
     * @return response DTO representing the completed goal
     * @throws GoalHasAlreadyCompleted if the goal is already marked as completed
     * @throws GoalNotFoundException   if the goal does not exist or does not belong to the user
     */
    @Override
    @Transactional
    public GoalResponseDto completeGoal(Long id, Long userId) {
        log.info("Completing goal id={} for userId={}", id, userId);
        Goal goalToComplete = findByIdAndUserId(id, userId);

        if (goalToComplete.isCompleted()) {
            log.warn("Goal id={} is already completed", id);
            throw new GoalHasAlreadyCompleted(
                    String.format(ErrorMessage.GOAL_COMPLETED, id)
            );
        }

        goalToComplete.setCompleted(true);
        for (Subgoal subGoal : goalToComplete.getSubgoals()) {
            if (!subGoal.isCompleted()) {
                subGoal.setCompleted(true);
            }
        }

        Goal updatedGoal = goalRepo.save(goalToComplete);
        log.info("Goal completed successfully: id={}, userId={}", updatedGoal.getId(), userId);
        return goalMapper.toGoalResponseDto(updatedGoal);
    }

    /**
     * Retrieves a specific goal belonging to the given user.
     *
     * @param id     goal identifier
     * @param userId identifier of the goal owner
     * @return response DTO representing the requested goal
     * @throws GoalNotFoundException if the goal does not exist or does not belong to the user
     */
    @Override
    public GoalResponseDto getGoal(Long id, Long userId) {
        log.info("Fetching goal id={} for userId={}", id, userId);
        Goal goal = findByIdAndUserId(id, userId);
        return goalMapper.toGoalResponseDto(goal);
    }

    /**
     * Removes a goal belonging to the specified user.
     *
     * @param id     goal identifier
     * @param userId identifier of the goal owner
     * @throws GoalNotFoundException if the goal does not exist or does not belong to the user
     */
    @Override
    @Transactional
    public void removeGoal(Long id, Long userId) {
        log.info("Deleting goal id={} for userId={}", id, userId);
        Goal goal = findByIdAndUserId(id, userId);
        goalRepo.delete(goal);
        log.info("Goal deleted successfully: id={}", id);
    }

    /**
     * Retrieves a paginated list of all non-completed goals for the specified user.
     *
     * @param pageable pagination and sorting configuration
     * @param userId   identifier of the goal owner
     * @return paginated response containing goal summaries
     */
    @Override
    public PageResponse<GoalSummaryDto> getAllGoals(Pageable pageable, Long userId) {
        log.info("Fetching all non-completed goals for userId={}", userId);
        Page<GoalSummaryDto> page =
                goalRepo.findAllNonCompletedGoalsWithSubCount(pageable, userId);
        return PageResponse.from(page);
    }

    /**
     * Retrieves a paginated list of all completed goals for the specified user.
     *
     * @param pageable pagination and sorting configuration
     * @param userId   identifier of the goal owner
     * @return paginated response containing completed goal summaries
     */
    @Override
    public PageResponse<GoalSummaryDto> getAllCompletedGoals(Pageable pageable, Long userId) {
        log.info("Fetching all completed goals for userId={}", userId);
        Page<GoalSummaryDto> page =
                goalRepo.findAllCompletedGoalsWithSubCount(pageable, userId);
        return PageResponse.from(page);
    }

    /**
     * Handles subgoal completion events.
     * <p>
     * Automatically marks the parent goal as completed if all related subgoals
     * are completed and the goal is not already marked as completed.
     *
     * @param event subgoal completion domain event
     */
    @EventListener
    @Transactional
    public void handleSubgoalCompleted(SubgoalCompletedEvent event) {
        Long parentGoalId = event.parentGoalId();
        log.info("Handling SubgoalCompletedEvent for parentGoalId={}", parentGoalId);

        Goal parentGoal = findGoalEntityById(parentGoalId);
        boolean allSubgoalsCompleted = parentGoal.getSubgoals().stream()
                .allMatch(Subgoal::isCompleted);

        if (allSubgoalsCompleted && !parentGoal.isCompleted()) {
            parentGoal.setCompleted(true);
            goalRepo.save(parentGoal);
            log.info(
                    "Parent goal id={} marked as completed due to all subgoals completed",
                    parentGoalId
            );
        }
    }

    /**
     * Finds a goal by its identifier and owner identifier.
     *
     * @param goalId goal identifier
     * @param userId identifier of the goal owner
     * @return goal entity
     * @throws GoalNotFoundException if the goal does not exist or does not belong to the user
     */
    @Override
    public Goal findByIdAndUserId(Long goalId, Long userId) {
        log.debug("Finding goal id={} for userId={}", goalId, userId);
        return goalRepo.findByGoalIdAndOwnerId(goalId, userId)
                .orElseThrow(() -> {
                    log.warn("Goal id={} not found for userId={}", goalId, userId);
                    return new GoalNotFoundException(
                            String.format(
                                    ErrorMessage.GOAL_WITH_THIS_OWNER_NOT_FOUND,
                                    goalId,
                                    userId
                            )
                    );
                });
    }

    /**
     * Finds a goal entity by its identifier.
     * <p>
     * Intended for internal use, including event handling.
     *
     * @param id goal identifier
     * @return goal entity
     * @throws GoalNotFoundException if the goal does not exist
     */
    private Goal findGoalEntityById(Long id) {
        log.debug("Finding goal entity by id={}", id);
        return goalRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Goal entity not found for id={}", id);
                    return new GoalNotFoundException(
                            String.format(ErrorMessage.GOAL_NOT_FOUND, id)
                    );
                });
    }
}
