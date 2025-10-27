package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubGoalResponseDto {
    private Integer id;
    private String title;
    private String description;
    private Difficulty difficulty;
    private Integer estimatedHours;
    private Boolean completed;
}
