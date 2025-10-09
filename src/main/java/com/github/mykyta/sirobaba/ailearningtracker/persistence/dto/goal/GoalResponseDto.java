package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class GoalResponseDto {
    private Long id;
    private String title;
    private Difficulty difficulty;
    private LocalDate deadline;
    private boolean completed;
}

