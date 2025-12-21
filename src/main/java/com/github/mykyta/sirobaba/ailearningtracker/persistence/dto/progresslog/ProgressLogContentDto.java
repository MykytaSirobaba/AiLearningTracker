package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog;

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
public class ProgressLogContentDto {
    private Long id;
    private String content;
}
