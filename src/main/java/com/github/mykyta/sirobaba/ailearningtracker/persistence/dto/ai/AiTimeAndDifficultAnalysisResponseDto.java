package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class AiTimeAndDifficultAnalysisResponseDto {
    public class AiAnalysisResponseDto {
        private String analyzedText;
        private Difficulty difficulty;
        private int totalHours;
        private List<SubGoalResponseDto> subgoals;
    }
}
