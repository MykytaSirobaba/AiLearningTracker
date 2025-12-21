package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoalRequestDto {
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;
    @Size(min = 10, max = 200, message = "Prompt must be between 10 and 200 characters")
    private String prompt;
    @NotNull(message = "Deadline cannot be null")
    private LocalDate deadline;

    @NotNull(message = "Hours per week cannot be null")
    @Min(value = 1, message = "Hours per week must be at least 1")
    @Max(value = 100, message = "Hours per week must be at most 100")
    private Integer hoursPerWeek;
}
