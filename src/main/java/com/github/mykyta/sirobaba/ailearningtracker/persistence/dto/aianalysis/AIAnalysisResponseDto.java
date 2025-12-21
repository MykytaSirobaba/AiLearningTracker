package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by Mykyta Sirobaba on 03.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResponseDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
