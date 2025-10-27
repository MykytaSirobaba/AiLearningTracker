package com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Created by Mykyta Sirobaba on 20.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Mapper(componentModel = "spring")
public interface SubgoalMapper {
    
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "completed", constant = "false")
    Subgoal toSubGoal(SubGoalResponseDto dto);

    SubGoalResponseDto toSubGoalResponseDto(Subgoal subGoal);
}
