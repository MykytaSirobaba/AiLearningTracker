package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisDetailsDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import org.springframework.data.domain.Pageable;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface AIAnalysisService {

    /**
     * Creates a new AI-based analysis for the user's goal progress.
     *
     * @param userId ID of the authenticated user
     * @param aiAnalysisRequestDto request containing analysis settings (e.g., log limit)
     * @param goalId ID of the goal for which analysis is being created
     * @return details of the created AI analysis
     */
    AIAnalysisDetailsDto createAIAnalysis(Long userId, AIAnalysisRequestDto aiAnalysisRequestDto, Long goalId);

    /**
     * Retrieves a specific AI analysis for the given user and goal.
     *
     * @param userId ID of the authenticated user
     * @param analysisId ID of the AI analysis to retrieve
     * @param goalId ID of the goal the analysis belongs to
     * @return detailed view of the AI analysis
     */
    AIAnalysisDetailsDto getAIAnalysis(Long userId, Long analysisId, Long goalId);

    /**
     * Returns a paginated list of AI analyses for the given goal.
     *
     * @param userId ID of the authenticated user
     * @param goalId ID of the goal to list analyses for
     * @param pageable pagination parameters
     * @return paginated response containing list of analyses
     */
    PageResponse<AIAnalysisResponseDto> getAIAnalyses(Long userId, Long goalId, Pageable pageable);

    /**
     * Deletes an AI analysis that belongs to the given user and goal.
     *
     * @param userId ID of the authenticated user
     * @param goalId ID of the goal associated with the analysis
     * @param analysisId ID of the analysis to delete
     */
    void deleteAIAnalysis(Long userId, Long goalId, Long analysisId);
}

