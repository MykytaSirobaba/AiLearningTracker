package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper responsible for converting between {@link Subgoal} entities and
 * {@link SubGoalResponseDto} DTOs.
 * <p>
 * Goal field is ignored when mapping from DTO → entity, because the parent Goal
 * must be injected manually in the service layer.
 */
@Mapper(componentModel = "spring")
public interface SubgoalMapper {

    /**
     * Converts SubGoalResponseDto into a new Subgoal entity.
     * <p>
     * Notes:
     * - The "goal" field is ignored and must be set manually.
     * - The "completed" field is set to false by default.
     *
     * @param dto the data transfer object containing basic subgoal info
     * @return a new Subgoal entity
     */
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "completed", constant = "false")
    Subgoal toSubGoal(SubGoalResponseDto dto);

    /**
     * Converts a Subgoal entity into SubGoalResponseDto.
     *
     * @param subGoal the entity to convert
     * @return converted DTO
     */
    SubGoalResponseDto toSubGoalResponseDto(Subgoal subGoal);
}

