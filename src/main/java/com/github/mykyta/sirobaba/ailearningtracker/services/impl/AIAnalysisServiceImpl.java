package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.AiAnalysisInThisGoalNotFound;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiAnalysisOfProgressLogDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisDetailsDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.AIAnalysis;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.AIAnalysisMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.AIAnalysisRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIAnalysisService;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIService;
import com.github.mykyta.sirobaba.ailearningtracker.services.GoalService;
import com.github.mykyta.sirobaba.ailearningtracker.services.ProgressLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing AI analyses of users' goals and progress logs.
 * <p>
 * This service handles creation, retrieval, listing, and deletion of AI analysis data.
 * It integrates with ProgressLogService, GoalService, and AIService to analyze progress
 * logs, map results, and persist them in the repository.
 */
@Service
@Slf4j
@AllArgsConstructor
public class AIAnalysisServiceImpl implements AIAnalysisService {

    private final AIAnalysisRepo aiAnalysisRepo;
    private final ProgressLogService progressLogService;
    private final GoalService goalService;
    private final AIService aiService;
    private final AIAnalysisMapper aiAnalysisMapper;

    /**
     * Creates a new AI analysis for the given goal and user.
     * Retrieves progress logs for the goal, performs AI analysis, and persists the result.
     *
     * @param userId ID of the user requesting the analysis
     * @param aiAnalysisRequestDto request containing analysis parameters (e.g., limit)
     * @param goalId ID of the goal to analyze
     * @return DTO containing detailed information about the created AI analysis
     * @throws AiAnalysisInThisGoalNotFound if the goal does not belong to the user
     */
    @Override
    @Transactional
    public AIAnalysisDetailsDto createAIAnalysis(Long userId,
                                                 AIAnalysisRequestDto aiAnalysisRequestDto,
                                                 Long goalId) {
        log.info("Creating AI analysis: userId={}, goalId={}, limit={}",
                userId, goalId, aiAnalysisRequestDto.getLimit());

        List<ProgressLogContentDto> progressLogContentDto =
                progressLogService.getProgressLogContent(goalId, userId, aiAnalysisRequestDto.getLimit());
        log.debug("Retrieved {} progress logs for goalId={}", progressLogContentDto.size(), goalId);

        Goal goal = goalService.findByIdAndUserId(goalId, userId);
        log.info("Goal found for analysis: {}", goal.getId());

        AiAnalysisOfProgressLogDto result = aiService.analyseProgressLog(
                progressLogContentDto,
                goal.getDescription(),
                goal.getSubgoals()
        );

        log.debug("AI analysis completed for goalId={}", goalId);

        AIAnalysis aiAnalysis = aiAnalysisMapper.toAIAnalysis(result);
        aiAnalysis.setGoal(goal);

        AIAnalysis saved = aiAnalysisRepo.save(aiAnalysis);
        log.info("AI analysis saved with id={}", saved.getId());

        return aiAnalysisMapper.toAIAnalysisDetailsDto(saved);
    }

    /**
     * Retrieves a single AI analysis by its ID for the given user's goal.
     *
     * @param userId ID of the user
     * @param analysisId ID of the AI analysis
     * @param goalId ID of the goal
     * @return DTO containing detailed information about the AI analysis
     * @throws AiAnalysisInThisGoalNotFound if the analysis does not belong to the goal
     */
    @Override
    public AIAnalysisDetailsDto getAIAnalysis(Long userId, Long analysisId, Long goalId) {
        log.info("Retrieving AI analysis: userId={}, analysisId={}, goalId={}", userId, analysisId, goalId);

        Goal goal = goalService.findByIdAndUserId(goalId, userId);
        log.info("Goal found: {}", goal.getId());

        AIAnalysis aiAnalysis = findByIdAndGoalId(analysisId, goal.getId());
        log.info("Analysis found: {}", aiAnalysis.getId());

        return aiAnalysisMapper.toAIAnalysisDetailsDto(aiAnalysis);
    }

    /**
     * Retrieves a paginated list of AI analyses for the given user's goal.
     *
     * @param userId ID of the user
     * @param goalId ID of the goal
     * @param pageable pagination and sorting information
     * @return page response containing AI analysis summaries
     */
    @Override
    public PageResponse<AIAnalysisResponseDto> getAIAnalyses(Long userId, Long goalId, Pageable pageable) {
        log.info("Retrieving paginated AI analyses: userId={}, goalId={}, page={}", userId, goalId, pageable.getPageNumber());

        Page<AIAnalysisResponseDto> page =
                aiAnalysisRepo.findByIdAndUserIdAndGoalId(userId, goalId, pageable);
        log.debug("Retrieved {} analyses for goalId={}", page.getContent().size(), goalId);

        return PageResponse.from(page);
    }

    /**
     * Deletes an AI analysis by its ID for the given user's goal.
     *
     * @param userId ID of the user
     * @param goalId ID of the goal
     * @param analysisId ID of the analysis to delete
     * @throws AiAnalysisInThisGoalNotFound if the analysis does not belong to the user's goal
     */
    @Override
    @Transactional
    public void deleteAIAnalysis(Long userId, Long goalId, Long analysisId) {
        log.info("Deleting AI analysis: userId={}, goalId={}, analysisId={}", userId, goalId, analysisId);

        Goal goal = goalService.findByIdAndUserId(goalId, userId);
        AIAnalysis aiAnalysis = findByIdAndGoalId(analysisId, goal.getId());

        aiAnalysisRepo.delete(aiAnalysis);
        log.info("AI analysis deleted: {}", analysisId);
    }

    /**
     * Finds an AI analysis by ID and goal ID, ensuring it belongs to the specified goal.
     *
     * @param analysisId ID of the AI analysis
     * @param goalId ID of the goal
     * @return AI analysis entity
     * @throws AiAnalysisInThisGoalNotFound if the analysis does not exist for the given goal
     */
    private AIAnalysis findByIdAndGoalId(Long analysisId, Long goalId) {
        return aiAnalysisRepo.findByIdAndGoalId(analysisId, goalId)
                .orElseThrow(() -> {
                    log.warn("AI analysis not found: analysisId={}, goalId={}", analysisId, goalId);
                    return new AiAnalysisInThisGoalNotFound(
                            String.format(ErrorMessage.AI_ANALYSIS_IN_THIS_GOAL_NOT_FOUND, analysisId, goalId)
                    );
                });
    }
}