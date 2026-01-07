package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.events.goal.SubgoalCompletedEvent;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.SubgoalHasAlreadyCompleted;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.SubgoalNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.SubgoalMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.SubgoalRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.SubgoalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * Service implementation for managing subgoals within learning goals.
 * Handles creating, linking, and completing subgoals, as well as publishing events
 * when subgoals are completed.
 */
@Slf4j
@Service
@AllArgsConstructor
public class SubgoalServiceImpl implements SubgoalService {

    private final SubgoalMapper subGoalMapper;
    private final SubgoalRepo subGoalRepo;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Completes a subgoal for a given user and publishes a SubgoalCompletedEvent.
     *
     * @param subgoalId ID of the subgoal to complete.
     * @param userId    ID of the user completing the subgoal.
     * @return SubGoalResponseDto representing the completed subgoal.
     * @throws SubgoalHasAlreadyCompleted if the subgoal is already completed.
     * @throws SubgoalNotFoundException   if the subgoal does not exist or does not belong to the user.
     */
    @Override
    @Transactional
    public SubGoalResponseDto completeSubgoal(Long subgoalId, Long userId) {
        log.info("User {} is attempting to complete subgoal {}", userId, subgoalId);

        Subgoal subgoal = findBySubgoalAndUserId(subgoalId, userId);

        if (subgoal.isCompleted()) {
            log.warn("Subgoal {} has already been completed by user {}", subgoalId, userId);
            throw new SubgoalHasAlreadyCompleted(
                    String.format(ErrorMessage.SUBGOAL_COMPLETED, subgoalId)
            );
        }

        subgoal.setCompleted(true);
        Subgoal savedSubgoal = subGoalRepo.save(subgoal);
        log.info("Subgoal {} completed successfully for user {}", subgoalId, userId);

        eventPublisher.publishEvent(new SubgoalCompletedEvent(subgoal.getGoal().getId()));
        log.debug("SubgoalCompletedEvent published for parent goal {}", subgoal.getGoal().getId());

        return subGoalMapper.toSubGoalResponseDto(savedSubgoal);
    }

    /**
     * Creates subgoals from DTOs and links them to a parent goal.
     *
     * @param subGoalDtos list of subgoal DTOs.
     * @param parentGoal  parent Goal entity.
     * @return list of Subgoal entities linked to the parent goal.
     */
    @Override
    public List<Subgoal> createAndLinkSubGoals(List<SubGoalResponseDto> subGoalDtos, Goal parentGoal) {
        if (subGoalDtos == null || subGoalDtos.isEmpty()) {
            log.warn("No subgoals provided for parent goal {}", parentGoal.getId());
            return Collections.emptyList();
        }

        List<Subgoal> subgoals = subGoalDtos.stream()
                .map(dto -> {
                    Subgoal subGoal = subGoalMapper.toSubGoal(dto);
                    subGoal.setGoal(parentGoal);
                    return subGoal;
                })
                .toList();

        log.info("Created {} subgoals for parent goal {}", subgoals.size(), parentGoal.getId());
        return subgoals;
    }

    /**
     * Finds a subgoal by its ID and verifies ownership.
     *
     * @param subGoalId ID of the subgoal.
     * @param ownerId   ID of the user who owns the subgoal.
     * @return Subgoal entity.
     * @throws SubgoalNotFoundException if the subgoal does not exist or does not belong to the user.
     */
    private Subgoal findBySubgoalAndUserId(Long subGoalId, Long ownerId) {
        return subGoalRepo.findBySubgoalAndUserId(subGoalId, ownerId)
                .orElseThrow(() -> {
                    log.error("Subgoal {} not found for user {}", subGoalId, ownerId);
                    return new SubgoalNotFoundException(
                            String.format(ErrorMessage.SUBGOAL_WITH_THIS_OWNER_NOT_FOUND, subGoalId, ownerId)
                    );
                });
    }
}
