package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

/**
 * Created by Mykyta Sirobaba on 10.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Mapper(componentModel = "spring", imports = LocalDate.class, uses = SubgoalMapper.class)
public interface GoalMapper {

    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "difficulty", target = "difficulty")
    @Mapping(target = "subgoals", ignore = true)
    @Mapping(source = "deadline", target = "deadline", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "id", ignore = true)
    Goal toGoal(AiPlanResponseDto aiPlanResponseDto);

    @Mapping(source = "estimatedHours", target = "estimatedHours")
    GoalResponseDto toGoalResponseDto(Goal goal);

//    Goal toGoal(GoalResponseDto goalResponseDto);
}