package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by Mykyta Sirobaba on 27.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalSummaryDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    private Long subGoalCount;
    private boolean completed;
}