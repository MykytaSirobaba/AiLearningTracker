package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by Mykyta Sirobaba on 17.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIAnalysisDetailsDto {
    private Long id;
    private String title;
    private String analysisText;
    private LocalDateTime createdAt;
}
