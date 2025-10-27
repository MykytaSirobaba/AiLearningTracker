package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPlanResponseDto {
    private String title;
    private String description;
    private Difficulty difficulty;
    private List<SubGoalResponseDto> subGoals;
    private LocalDate deadline;
}