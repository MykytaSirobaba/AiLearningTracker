package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import org.springframework.data.domain.Pageable;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface GoalService {

    /**
     * Method for creating a new learning goal with AI-generated plan and subgoals.
     *
     * @param goalRequestDto - request body containing goal data.
     * @param user           - information about the current user.
     * @return GoalResponseDto representing the created goal.
     */
    GoalResponseDto createGoal(GoalRequestDto goalRequestDto, CurrentUserInfoDto user);

    /**
     * Method for retrieving all non-completed user goals with pagination.
     *
     * @param pageable - pagination parameters.
     * @param userId   - id of the user.
     * @return paginated list of goals as GoalSummaryDto.
     */
    PageResponse<GoalSummaryDto> getAllGoals(Pageable pageable, Long userId);

    /**
     * Method for retrieving detailed data of a specific goal.
     *
     * @param id     - goal id.
     * @param userId - id of the user who owns the goal.
     * @return GoalResponseDto with goal information.
     */
    GoalResponseDto getGoal(Long id, Long userId);

    /**
     * Method for marking a goal as completed.
     *
     * @param id     - goal id.
     * @param userId - id of the user.
     * @return GoalResponseDto representing the completed goal.
     */
    GoalResponseDto completeGoal(Long id, Long userId);

    /**
     * Method for retrieving all completed goals with pagination.
     *
     * @param pageable - pagination settings.
     * @param userId   - user id.
     * @return PageResponse of completed goals.
     */
    PageResponse<GoalSummaryDto> getAllCompletedGoals(Pageable pageable, Long userId);

    /**
     * Method for retrieving a goal by id and user id.
     *
     * @param goalId - goal id.
     * @param userId - user id.
     * @return Goal entity.
     */
    Goal findByIdAndUserId(Long goalId, Long userId);

    /**
     * Method for deleting a goal.
     *
     * @param id     - goal id.
     * @param userId - user id.
     */
    void removeGoal(Long id, Long userId);
}
