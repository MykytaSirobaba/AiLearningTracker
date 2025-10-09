package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal;

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
public class GoalRequestDto {
    private String title;
    private String prompt;
    private Integer availableWeeks;
}
