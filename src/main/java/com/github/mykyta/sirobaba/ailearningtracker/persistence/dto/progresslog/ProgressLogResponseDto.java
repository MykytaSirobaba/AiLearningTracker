package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressLogResponseDto {
    private Long progressLogId;
    private String title;
    private LocalDateTime logTime;
    private Integer totalMinutes;
    private String formattedTime;

}
