package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai;

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
public class AiAnalysisOfProgressLogDto {
    private String title;
    private String analysisText;
    private LocalDateTime createdAt;
}
