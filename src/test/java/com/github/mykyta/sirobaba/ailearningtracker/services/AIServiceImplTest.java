package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mykyta.sirobaba.ailearningtracker.configs.AiTemplateConfig;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.AiJsonParseException;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.GoalValidationException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiAnalysisOfProgressLogDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.AiPlanResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.GoalAiResultDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.ai.SubgoalListAiResultDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.subgoal.SubGoalResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Difficulty;
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.AIServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag("Service")
@DisplayName("Artificial intelligence service test")
@ExtendWith(MockitoExtension.class)
class AIServiceImplTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;
    @Mock
    private AiTemplateConfig aiTemplateConfig;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AIServiceImpl aiService;

    private GoalRequestDto goalRequestDto;

    @BeforeEach
    void setUp() {
        goalRequestDto = GoalRequestDto.builder()
                .title("Learn Spring Boot in depth")
                .prompt("Create a Spring Boot learning plan")
                .deadline(LocalDate.now().plusWeeks(10))
                .hoursPerWeek(10)
                .build();
    }

    @Test
    @DisplayName("createLearningPlan() — should return valid AiPlanResponseDto when AI returns correct JSON")
    void createLearningPlan_shouldReturnValidPlan() throws Exception {
        String mainGoalTemplate = "main template {{goal}}";
        String subgoalTemplate = "sub template {{generalGoal}}";

        when(aiTemplateConfig.getTemplate("createMainGoal")).thenReturn(mainGoalTemplate);
        when(aiTemplateConfig.getTemplate("createSubgoal")).thenReturn(subgoalTemplate);

        String aiMainResponse = "{\"generalGoal\":\"Master Spring Boot\",\"difficulty\":\"MEDIUM\"}";
        String aiSubResponse = "{\"subGoals\":[{\"title\":\"Learn Basics\",\"difficulty\":\"EASY\",\"estimatedHours\":10}]}";

        when(chatClient.prompt(anyString()).call().content()).thenReturn(aiMainResponse).thenReturn(aiSubResponse);

        GoalAiResultDto mainGoalResult = GoalAiResultDto.builder()
                .generalGoal("Master Spring Boot")
                .difficulty(Difficulty.MEDIUM)
                .deadline(goalRequestDto.getDeadline())
                .build();

        SubgoalListAiResultDto subgoalList = SubgoalListAiResultDto.builder()
                .subGoals(List.of(SubGoalResponseDto.builder()
                        .title("Learn Basics")
                        .difficulty(Difficulty.EASY)
                        .estimatedHours(10)
                        .build()))
                .build();

        when(objectMapper.readValue(aiMainResponse, GoalAiResultDto.class)).thenReturn(mainGoalResult);
        when(objectMapper.readValue(aiSubResponse, SubgoalListAiResultDto.class)).thenReturn(subgoalList);

        AiPlanResponseDto result = aiService.createLearningPlan(goalRequestDto);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(goalRequestDto.getTitle());
        assertThat(result.getDescription()).isEqualTo("Master Spring Boot");
        assertThat(result.getDifficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(result.getDeadline()).isEqualTo(goalRequestDto.getDeadline());
    }

    @Test
    @DisplayName("createLearningPlan() — should throw AiJsonParseException if AI response cannot be parsed")
    void createLearningPlan_shouldThrowWhenJsonInvalid() throws Exception {
        when(aiTemplateConfig.getTemplate("createMainGoal")).thenReturn("template");
        when(chatClient.prompt(anyString()).call().content()).thenReturn("{invalid_json");

        when(objectMapper.readValue(anyString(), eq(GoalAiResultDto.class)))
                .thenThrow(new RuntimeException("JSON parse error"));

        assertThatThrownBy(() -> aiService.createLearningPlan(goalRequestDto))
                .isInstanceOf(AiJsonParseException.class)
                .hasMessageContaining("Failed to parse AI JSON");
    }

    @Test
    @DisplayName("createLearningPlan() — should throw GoalValidationException if deadline unrealistic")
    void createLearningPlan_shouldThrowGoalValidationException() throws Exception {
        String aiMainResponse = "{\"generalGoal\":\"Learn AI\",\"difficulty\":\"HARD\"}";
        String aiSubResponse = "{\"subGoals\":[{\"title\":\"Study ML\",\"difficulty\":\"HARD\",\"estimatedHours\":500}]}";

        when(aiTemplateConfig.getTemplate("createMainGoal")).thenReturn("template");
        when(aiTemplateConfig.getTemplate("createSubgoal")).thenReturn("template");

        when(chatClient.prompt(anyString()).call().content())
                .thenReturn(aiMainResponse)
                .thenReturn(aiSubResponse);

        GoalAiResultDto mainGoal = GoalAiResultDto.builder()
                .generalGoal("Learn AI")
                .difficulty(Difficulty.HARD)
                .deadline(goalRequestDto.getDeadline())
                .build();

        SubgoalListAiResultDto subgoals = SubgoalListAiResultDto.builder()
                .subGoals(List.of(SubGoalResponseDto.builder()
                        .title("Study ML")
                        .difficulty(Difficulty.HARD)
                        .estimatedHours(500)
                        .build()))
                .build();

        when(objectMapper.readValue(aiMainResponse, GoalAiResultDto.class)).thenReturn(mainGoal);
        when(objectMapper.readValue(aiSubResponse, SubgoalListAiResultDto.class)).thenReturn(subgoals);

        assertThatThrownBy(() -> aiService.createLearningPlan(goalRequestDto))
                .isInstanceOf(GoalValidationException.class)
                .hasMessageContaining("Unrealistic deadline");
    }

    @Test
    @DisplayName("analyseProgressLog() — should return AiAnalysisOfProgressLogDto when AI returns valid JSON")
    void analyseProgressLog_shouldReturnValidAnalysis() throws Exception {
        String template = "analyse template {{goalDescription}}";
        when(aiTemplateConfig.getTemplate("createAnalysisProgressLogs")).thenReturn(template);

        String aiResponse = "{\"title\":\"Progress Summary\",\"analysisText\":\"Good progress overall\"}";
        when(chatClient.prompt(anyString()).call().content()).thenReturn(aiResponse);

        AiAnalysisOfProgressLogDto dto = AiAnalysisOfProgressLogDto.builder()
                .title("Progress Summary")
                .analysisText("Good progress overall")
                .createdAt(LocalDateTime.now())
                .build();

        when(objectMapper.readValue(aiResponse, AiAnalysisOfProgressLogDto.class)).thenReturn(dto);

        List<ProgressLogContentDto> progress = List.of(
                ProgressLogContentDto.builder().id(1L).content("Learned Spring Security").build()
        );

        AiAnalysisOfProgressLogDto result = aiService.analyseProgressLog(progress, "Spring Boot app", List.of());

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Progress Summary");
        assertThat(result.getAnalysisText()).isEqualTo("Good progress overall");
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("analyseProgressLog() — should throw AiJsonParseException if AI response invalid")
    void analyseProgressLog_shouldThrowAiJsonParseException() throws Exception {
        String template = "analyse template {{goalDescription}}";
        when(aiTemplateConfig.getTemplate("createAnalysisProgressLogs")).thenReturn(template);

        String invalidJson = "{not-valid-json";
        when(chatClient.prompt(anyString()).call().content()).thenReturn(invalidJson);
        when(objectMapper.readValue(anyString(), eq(AiAnalysisOfProgressLogDto.class)))
                .thenThrow(new RuntimeException("Parse error"));

        assertThatThrownBy(() ->
                aiService.analyseProgressLog(List.of(), "desc", List.of())
        ).isInstanceOf(AiJsonParseException.class)
                .hasMessageContaining("Failed to parse AI JSON");
    }
}