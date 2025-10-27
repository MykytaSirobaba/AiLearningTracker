package com.github.mykyta.sirobaba.ailearningtracker.service;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface AIService {
    AiPlanResponseDto createLearningPlan(GoalRequestDto goalRequestDto);

}
