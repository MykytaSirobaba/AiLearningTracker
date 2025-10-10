package com.github.mykyta.sirobaba.ailearningtracker.service.impl;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiTimeAndDifficultAnalysisRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiTimeAndDifficultAnalysisResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.service.AIService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
@AllArgsConstructor
public class AIServiceImpl implements AIService {

    private final ChatModel chatModel;

    @Override
    public AiPlanResponseDto createLearningPlan(GoalRequestDto goalRequestDto) {
        return null;
    }

    @Override
    public AiTimeAndDifficultAnalysisResponseDto analyzePlan(AiTimeAndDifficultAnalysisRequestDto aiAnalysisRequestDto) {
        return null;
    }
}
