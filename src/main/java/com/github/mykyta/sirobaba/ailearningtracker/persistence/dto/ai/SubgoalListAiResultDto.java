package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 20.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubgoalListAiResultDto {
    private List<SubGoalResponseDto> subGoals;
}