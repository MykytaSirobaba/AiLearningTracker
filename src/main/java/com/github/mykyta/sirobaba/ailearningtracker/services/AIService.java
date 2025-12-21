package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiAnalysisOfProgressLogDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface AIService {

    /**
     * Generates a personalized learning plan based on the provided goal request.
     *
     * @param goalRequestDto user-defined goal request
     * @return AI-generated learning plan including title, description, difficulty,
     *         deadline, and detailed subgoals
     */
    AiPlanResponseDto createLearningPlan(GoalRequestDto goalRequestDto);

    /**
     * Analyzes user progress logs with help of the AI model and produces insights
     * and recommendations.
     *
     * @param progressLogContentDtos list of progress entries made by the user
     * @param description description of the parent goal
     * @param subgoals list of subgoals under the parent goal
     * @return AI-generated analysis of the user's progress, including recommendations
     */
    AiAnalysisOfProgressLogDto analyseProgressLog(List<ProgressLogContentDto> progressLogContentDtos,
                                                  String description,
                                                  List<Subgoal> subgoals);
}

