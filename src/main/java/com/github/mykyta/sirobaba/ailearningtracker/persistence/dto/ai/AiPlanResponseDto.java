package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class AiPlanResponseDto {
    private String text;
}
