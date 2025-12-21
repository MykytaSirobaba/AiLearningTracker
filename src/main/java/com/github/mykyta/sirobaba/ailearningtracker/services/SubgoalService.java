package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.SubgoalHasAlreadyCompleted;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.SubgoalNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 20.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface SubgoalService {

    /**
     * Method for creating and linking subgoals to the parent goal.
     *
     * @param subGoalDtos - list of DTOs containing subgoal data.
     * @param parentGoal  - parent Goal entity.
     * @return list of created Subgoal entities linked to parent goal.
     */
    List<Subgoal> createAndLinkSubGoals(List<SubGoalResponseDto> subGoalDtos, Goal parentGoal);

    /**
     * Method for completing a subgoal.
     *
     * @param id     - subgoal id.
     * @param userId - id of the user who owns the subgoal.
     * @return SubGoalResponseDto with updated subgoal information.
     * @throws SubgoalHasAlreadyCompleted if the subgoal is already completed.
     * @throws SubgoalNotFoundException if subgoal does not belong to this user or is not found.
     */
    SubGoalResponseDto completeSubgoal(Long id, Long userId);
}

