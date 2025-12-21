package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Mykyta Sirobaba on 03.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisRequestDto {
    @Min(1)
    @Max(50)
    private Integer limit;
}
