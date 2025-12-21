package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressLogDetailsResponseDto {
    private Long progressLogId;
    private String title;
    private LocalDate logTime;
    private Integer totalMinutes;
    private String formattedTime;
    private String note;
}
