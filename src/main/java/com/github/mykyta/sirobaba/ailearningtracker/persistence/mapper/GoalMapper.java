package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

/**
 * Mapper for converting AI-planned goal DTOs into {@link Goal} entities and
 * converting Goal entities into response DTOs.
 * <p>
 * Subgoals are ignored during creation and must be mapped separately.
 */
@Mapper(
        componentModel = "spring",
        imports = LocalDate.class,
        uses = SubgoalMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface GoalMapper {

    /**
     * Converts an AI plan response DTO into a Goal entity.
     * <p>
     * Notes:
     * - Subgoals are ignored because AI provides them separately.
     * - Deadline is converted using the format yyyy-MM-dd.
     * - ID is ignored to allow JPA to generate it.
     * - Goal is always created with completed=false.
     *
     * @param aiPlanResponseDto dto from AI service
     * @return a new Goal entity
     */
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "difficulty", target = "difficulty")
    @Mapping(target = "subgoals", ignore = true)
    @Mapping(source = "deadline", target = "deadline", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "id", ignore = true)
    Goal toGoal(AiPlanResponseDto aiPlanResponseDto);

    /**
     * Converts a Goal entity into a GoalResponseDto.
     *
     * @param goal the entity to convert
     * @return response DTO
     */
    @Mapping(source = "estimatedHours", target = "estimatedHours")
    GoalResponseDto toGoalResponseDto(Goal goal);
}
