package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by Mykyta Sirobaba on 20.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalAiResultDto {
    private String generalGoal;
    private Difficulty difficulty;
    private LocalDate deadline;
}

