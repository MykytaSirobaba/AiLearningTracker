package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal;

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
@AllArgsConstructor
@NoArgsConstructor
public class GoalResponseDto {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private LocalDate deadline;
    private Integer estimatedHours;
    private List<SubGoalResponseDto> subgoals;
    private boolean completed;
}

