package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.AiTemplateConfig;
import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.constants.TemplateNames;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.AiJsonParseException;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.GoalValidationException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiAnalysisOfProgressLogDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.GoalAiResultDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.SubgoalListAiResultDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import com.github.mykyta.sirobaba.ailearningtracker.services.AIService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service implementation for interacting with the AI model to generate learning plans
 * and analyze progress logs.
 * <p>
 * Provides functionality to create main goals, subgoals, validate deadlines,
 * and parse AI responses into DTOs.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AIServiceImpl implements AIService {

    private final ChatModel chatModel;
    private final AiTemplateConfig aiTemplateConfig;
    private final ObjectMapper objectMapper;

    /**
     * Creates a complete learning plan including main goal and subgoals.
     *
     * @param goalRequestDto user-provided goal request
     * @return AI-generated learning plan response
     * @throws AiJsonParseException if AI returns invalid JSON
     * @throws GoalValidationException if the user's deadline is unrealistic
     */
    @Override
    public AiPlanResponseDto createLearningPlan(GoalRequestDto goalRequestDto) {
        log.info("Creating learning plan for goal: {}", goalRequestDto.getTitle());

        GoalAiResultDto goalAiResultDto = createMainGoal(goalRequestDto);
        SubgoalListAiResultDto subgoalListResult = createSubgoal(goalAiResultDto);
        validateDeadline(goalAiResultDto, subgoalListResult, goalRequestDto);

        AiPlanResponseDto plan = AiPlanResponseDto.builder()
                .title(goalRequestDto.getTitle())
                .description(goalAiResultDto.getGeneralGoal())
                .difficulty(goalAiResultDto.getDifficulty())
                .subGoals(subgoalListResult.getSubGoals())
                .deadline(goalAiResultDto.getDeadline())
                .build();

        log.info("Learning plan created successfully for goal: {}", goalRequestDto.getTitle());
        return plan;
    }

    /**
     * Requests the AI model to generate a main goal structure.
     *
     * @param goalRequestDto user goal input
     * @return parsed main goal metadata
     * @throws AiJsonParseException if AI returns malformed JSON
     */
    private GoalAiResultDto createMainGoal(GoalRequestDto goalRequestDto) {
        log.debug("Generating main goal for user input: {}", goalRequestDto.getPrompt());
        String template = aiTemplateConfig.getTemplate(TemplateNames.CREATE_MAIN_GOAL);
        String goal = goalRequestDto.getPrompt() != null ? goalRequestDto.getPrompt() : "";

        String promptText = template.replace("{{goal}}", goal);
        String aiResponse = chatModel.call(promptText);
        String jsonString = getString(aiResponse);

        try {
            GoalAiResultDto result = objectMapper.readValue(jsonString, GoalAiResultDto.class);
            result.setDeadline(goalRequestDto.getDeadline());
            log.debug("Main goal parsed successfully: {}", result.getGeneralGoal());
            return result;
        } catch (Exception e) {
            log.error("Failed to parse main goal JSON from AI response: {}", aiResponse, e);
            throw new AiJsonParseException(String.format(ErrorMessage.AI_JSON_PARSE, aiResponse));
        }
    }

    /**
     * Requests the AI model to generate subgoals based on the main goal.
     *
     * @param goalAiResultDto AI-generated main goal
     * @return list of parsed subgoals
     * @throws AiJsonParseException if JSON parsing fails
     */
    private SubgoalListAiResultDto createSubgoal(GoalAiResultDto goalAiResultDto) {
        log.debug("Generating subgoals for main goal: {}", goalAiResultDto.getGeneralGoal());
        String template = aiTemplateConfig.getTemplate(TemplateNames.CREATE_SUBGOAL);

        String promptText = template
                .replace("{{generalGoal}}", goalAiResultDto.getGeneralGoal())
                .replace("{{difficulty}}", goalAiResultDto.getDifficulty().toString());

        String aiResponse = chatModel.call(promptText);
        String jsonString = getString(aiResponse);

        try {
            SubgoalListAiResultDto result = objectMapper.readValue(jsonString, SubgoalListAiResultDto.class);
            log.debug("Subgoals parsed successfully, count: {}", result.getSubGoals().size());
            return result;
        } catch (Exception e) {
            log.error("Failed to parse subgoals JSON from AI response: {}", aiResponse, e);
            throw new AiJsonParseException(String.format(ErrorMessage.AI_JSON_PARSE, aiResponse));
        }
    }

    /**
     * Validates whether the user's deadline is feasible given estimated hours for subgoals.
     *
     * @param goalAiResultDto AI-generated main goal
     * @param subgoalListResult list of subgoals
     * @param goalRequestDto original user request
     * @throws GoalValidationException if the deadline is insufficient
     */
    private void validateDeadline(GoalAiResultDto goalAiResultDto,
                                  SubgoalListAiResultDto subgoalListResult,
                                  GoalRequestDto goalRequestDto) {

        log.debug("Validating deadline for goal: {}", goalAiResultDto.getGeneralGoal());
        int totalEstimatedHours = subgoalListResult.getSubGoals().stream()
                .mapToInt(sg -> {
                    Integer aiEstimatedHours = sg.getEstimatedHours();
                    return (aiEstimatedHours == null || aiEstimatedHours <= 0)
                            ? sg.getDifficulty().getEstimatedHours()
                            : aiEstimatedHours;
                })
                .sum();

        LocalDate deadline = goalAiResultDto.getDeadline();
        Integer hoursPerWeek = goalRequestDto.getHoursPerWeek();

        long availableDays = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        double availableWeeks = availableDays / 7.0;
        double totalAvailableHours = availableWeeks * hoursPerWeek;

        if (totalAvailableHours < totalEstimatedHours) {
            String formattedAvailableHours = String.format("%.1f", totalAvailableHours);
            log.warn("Unrealistic deadline: totalEstimated={}, available={}, deadline={}",
                    totalEstimatedHours, formattedAvailableHours, deadline);

            throw new GoalValidationException(
                    String.format(
                            ErrorMessage.UNREALISTIC_DEADLINE,
                            totalEstimatedHours,
                            formattedAvailableHours,
                            hoursPerWeek,
                            deadline
                    )
            );
        }

        log.debug("Deadline validation passed for goal: {}", goalAiResultDto.getGeneralGoal());
    }

    /**
     * Analyzes a list of progress logs using the AI model.
     *
     * @param progressLogContentDtos progress logs to analyze
     * @param description goal description
     * @param subgoals list of subgoals
     * @return AI-generated analysis DTO
     * @throws AiJsonParseException if AI returns invalid JSON
     */
    @Override
    public AiAnalysisOfProgressLogDto analyseProgressLog(List<ProgressLogContentDto> progressLogContentDtos,
                                                         String description,
                                                         List<Subgoal> subgoals) {
        log.info("Analyzing progress logs for goal: {}", description);

        String template = aiTemplateConfig.getTemplate(TemplateNames.CREATE_ANALYSE_PROGRESS_LOGS);

        String promptText = template
                .replace("{{goalDescription}}", description)
                .replace("{{subgoals}}", subgoals.toString())
                .replace("{{progressLogContent}}", progressLogContentDtos.toString());

        String aiResponse = chatModel.call(promptText);
        String jsonString = getString(aiResponse);

        try {
            AiAnalysisOfProgressLogDto result =
                    objectMapper.readValue(jsonString, AiAnalysisOfProgressLogDto.class);
            result.setCreatedAt(LocalDateTime.now());
            log.info("Progress log analysis completed successfully for goal: {}", description);
            return result;
        } catch (Exception e) {
            log.error("Failed to parse AI analysis JSON for goal: {}", description, e);
            throw new AiJsonParseException(String.format(ErrorMessage.AI_JSON_PARSE, aiResponse));
        }
    }

    /**
     * Extracts JSON from AI responses, handling markdown code fences if present.
     *
     * @param aiResponse raw AI response
     * @return cleaned JSON string
     * @throws AiJsonParseException if response is not parsable JSON
     */
    @NotNull
    private static String getString(String aiResponse) {
        String jsonString = aiResponse;

        if (aiResponse.trim().startsWith("```")) {
            int startIndex = aiResponse.indexOf('{');
            int endIndex = aiResponse.lastIndexOf('}');
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonString = aiResponse.substring(startIndex, endIndex + 1);
            } else {
                throw new AiJsonParseException(
                        String.format(ErrorMessage.AI_RETURNED_NOT_PARSABLE_CONTENT, aiResponse)
                );
            }
        } else if (!aiResponse.trim().startsWith("{")) {
            throw new AiJsonParseException(
                    String.format(ErrorMessage.AI_NOT_RETURN_JSON, aiResponse)
            );
        }
        return jsonString;
    }
}
