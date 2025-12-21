package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressLogRequestDto {

    @Size(max = 100)
    private String title;

    @Min(value = 0, message = "Hours must be 0 or greater")
    @Max(value = 100, message = "Hours cannot exceed 100")
    private Integer hours;

    @Min(value = 0, message = "Minutes must be 0 or greater")
    @Max(value = 59, message = "Minutes cannot exceed 59")
    private Integer minutes;

    @Size(min = 20, max = 1000)
    private String note;
}
