package com.github.mykyta.sirobaba.ailearningtracker.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.config.AiTemplateConfig;
import com.github.mykyta.sirobaba.ailearningtracker.constant.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.constant.TemplateNames;
import com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions.AiJsonParseException;
import com.github.mykyta.sirobaba.ailearningtracker.exception.exceptions.GoalValidationException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.GoalAiResultDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.SubgoalListAiResultDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.service.AIService;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Created by Mykyta Sirobaba on 09.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Service
@AllArgsConstructor
public class AIServiceImpl implements AIService {

    private final ChatModel chatModel;
    private final AiTemplateConfig aiTemplateConfig;
    private final ObjectMapper objectMapper;

    @Override
    public AiPlanResponseDto createLearningPlan(GoalRequestDto goalRequestDto) {
        GoalAiResultDto goalAiResultDto = createMainGoal(goalRequestDto);
        SubgoalListAiResultDto subgoalListResult = createSubgoal(goalAiResultDto);
        validateDeadline(goalAiResultDto, subgoalListResult, goalRequestDto );

        return AiPlanResponseDto.builder()
                .title(goalRequestDto.getTitle())
                .description(goalAiResultDto.getGeneralGoal())
                .difficulty(goalAiResultDto.getDifficulty())
                .subGoals(subgoalListResult.getSubGoals())
                .deadline(goalAiResultDto.getDeadline())
                .build();
    }

    private GoalAiResultDto createMainGoal(GoalRequestDto goalRequestDto) {
        String template = aiTemplateConfig.getTemplate(TemplateNames.CREATE_MAIN_GOAL);

        String goal = goalRequestDto.getPrompt() != null ? goalRequestDto.getPrompt() : "";

        String promptText = template
                .replace("{{goal}}", goal);

        String aiResponse = chatModel.call(promptText);
        String jsonString = getString(aiResponse);

        try {
            GoalAiResultDto result = objectMapper.readValue(jsonString, GoalAiResultDto.class);
            result.setDeadline(goalRequestDto.getDeadline());
            return result;
        } catch (Exception e) {
            throw new AiJsonParseException(String.format(ErrorMessage.AI_JSON_PARSE, aiResponse));
        }
    }

    private SubgoalListAiResultDto createSubgoal(GoalAiResultDto goalAiResultDto) {
        String template = aiTemplateConfig.getTemplate(TemplateNames.CREATE_SUBGOAL);

        String promptText = template
                .replace("{{generalGoal}}", goalAiResultDto.getGeneralGoal())
                .replace("{{difficulty}}", goalAiResultDto.getDifficulty().toString());

        String aiResponse = chatModel.call(promptText);
        String jsonString = getString(aiResponse);

        try {
            return objectMapper.readValue(jsonString, SubgoalListAiResultDto.class);
        } catch (Exception e) {
            throw new AiJsonParseException(String.format(ErrorMessage.AI_JSON_PARSE, aiResponse));
        }
    }

    private void validateDeadline(GoalAiResultDto goalAiResultDto,
                                  SubgoalListAiResultDto subgoalListResult,
                                  GoalRequestDto goalRequestDto) {

        int totalEstimatedHours = subgoalListResult.getSubGoals().stream()
                .mapToInt(sg -> {
                    Integer aiEstimatedHours = sg.getEstimatedHours();

                    if (aiEstimatedHours == null || aiEstimatedHours <= 0) {
                        return sg.getDifficulty().getEstimatedHours();
                    }
                    return aiEstimatedHours;
                })
                .sum();

        LocalDate deadline = goalAiResultDto.getDeadline();
        Integer hoursPerWeek = goalRequestDto.getHoursPerWeek();

        long availableDays = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        double availableWeeks = availableDays / 7.0;
        double totalAvailableHours = availableWeeks * hoursPerWeek;

        if (totalAvailableHours < totalEstimatedHours) {
            String formattedAvailableHours = String.format("%.1f", totalAvailableHours);

            throw new GoalValidationException(
                    String.format(ErrorMessage.UNREALISTIC_DEADLINE,
                            totalEstimatedHours, formattedAvailableHours, hoursPerWeek, deadline)
            );
        }
    }

    @NotNull
    private static String getString(String aiResponse) {
        String jsonString = aiResponse;

        if (aiResponse.trim().startsWith("```")) {
            int startIndex = aiResponse.indexOf('{');
            int endIndex = aiResponse.lastIndexOf('}');
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                jsonString = aiResponse.substring(startIndex, endIndex + 1);
            } else {
                throw new AiJsonParseException(String.format(ErrorMessage.AI_RETURNED_NOT_PARSABLE_CONTENT, aiResponse));
            }
        } else if (!aiResponse.trim().startsWith("{")) {
            throw new AiJsonParseException(String.format(ErrorMessage.AI_NOT_RETURN_JSON, aiResponse));
        }
        return jsonString;
    }
}