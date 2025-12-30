package com.github.mykyta.sirobaba.ailearningtracker.services;

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
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.AIAnalysisServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.github.mykyta.sirobaba.ailearningtracker.ModelUtils.createTestAIAnalysis;
import static com.github.mykyta.sirobaba.ailearningtracker.ModelUtils.createTestGoal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class AIAnalysisServiceImplTest {

    @Mock
    private AIAnalysisRepo aiAnalysisRepo;
    @Mock
    private ProgressLogService progressLogService;
    @Mock
    private GoalService goalService;
    @Mock
    private AIService aiService;
    @Mock
    private AIAnalysisMapper aiAnalysisMapper;

    @InjectMocks
    private AIAnalysisServiceImpl aiAnalysisService;

    private Goal testGoal;
    private AIAnalysis testAIAnalysis;
    private AIAnalysisRequestDto requestDto;
    private AIAnalysisResponseDto responseDto;
    private AIAnalysisDetailsDto aiAnalysisDetailsDto;

    @BeforeEach
    void setUp() {
        testGoal = createTestGoal();
        testAIAnalysis = createTestAIAnalysis();
        requestDto = AIAnalysisRequestDto.builder().limit(5).build();
        responseDto = AIAnalysisResponseDto.builder()
                .id(1L)
                .title("Test Analysis")
                .createdAt(LocalDateTime.now())
                .build();
        aiAnalysisDetailsDto = AIAnalysisDetailsDto.builder()
                .id(1L)
                .title("Test Analysis")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("createAIAnalysis() — creates analysis and returns DTO")
    void createAIAnalysis_shouldCreateAndReturnDto() {
        Long userId = 1L;
        Long goalId = 1L;
        List<ProgressLogContentDto> content = List.of(ProgressLogContentDto.builder().content("log content").build());
        AiAnalysisOfProgressLogDto aiResult = AiAnalysisOfProgressLogDto.builder()
                .analysisText("AI Analysis")
                .createdAt(LocalDateTime.now())
                .build();

        when(progressLogService.getProgressLogContent(goalId, userId, requestDto.getLimit())).thenReturn(content);
        when(goalService.findByIdAndUserId(goalId, userId)).thenReturn(testGoal);
        when(aiService.analyseProgressLog(content, testGoal.getDescription(), testGoal.getSubgoals())).thenReturn(aiResult);
        when(aiAnalysisMapper.toAIAnalysis(aiResult)).thenReturn(testAIAnalysis);
        when(aiAnalysisRepo.save(testAIAnalysis)).thenReturn(testAIAnalysis);
        when(aiAnalysisMapper.toAIAnalysisDetailsDto(testAIAnalysis)).thenReturn(aiAnalysisDetailsDto);

        AIAnalysisDetailsDto result = aiAnalysisService.createAIAnalysis(userId, requestDto, goalId);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        verify(aiAnalysisRepo, times(1)).save(testAIAnalysis);
        verify(aiAnalysisMapper, times(1)).toAIAnalysisDetailsDto(testAIAnalysis);
    }

    @Test
    @DisplayName("getAIAnalysis() — returns AIAnalysisResponseDto if found")
    void getAIAnalysis_shouldReturnResponseDto() {
        Long userId = 1L;
        Long goalId = 1L;
        Long analysisId = 1L;

        when(goalService.findByIdAndUserId(goalId, userId)).thenReturn(testGoal);
        when(aiAnalysisRepo.findByIdAndGoalId(analysisId, testGoal.getId())).thenReturn(Optional.of(testAIAnalysis));
        when(aiAnalysisMapper.toAIAnalysisDetailsDto(testAIAnalysis)).thenReturn(aiAnalysisDetailsDto);

        AIAnalysisDetailsDto result = aiAnalysisService.getAIAnalysis(userId, analysisId, goalId);

        assertEquals(responseDto.getId(), result.getId());
        verify(aiAnalysisRepo).findByIdAndGoalId(analysisId, testGoal.getId());
    }

    @Test
    @DisplayName("getAIAnalyses() — returns page response of analyses")
    void getAIAnalyses_shouldReturnPageResponse() {
        Long userId = 1L;
        Long goalId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<AIAnalysisResponseDto> page = new PageImpl<>(List.of(responseDto));

        when(aiAnalysisRepo.findByIdAndUserIdAndGoalId(userId, goalId, pageable)).thenReturn(page);

        PageResponse<AIAnalysisResponseDto> result = aiAnalysisService.getAIAnalyses(userId, goalId, pageable);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(responseDto.getTitle(), result.content().getFirst().getTitle());
        verify(aiAnalysisRepo).findByIdAndUserIdAndGoalId(userId, goalId, pageable);
    }

    @Test
    @DisplayName("deleteAIAnalysis() — deletes analysis if found")
    void deleteAIAnalysis_shouldDeleteIfFound() {
        Long userId = 1L;
        Long goalId = 1L;
        Long analysisId = 1L;

        when(goalService.findByIdAndUserId(goalId, userId)).thenReturn(testGoal);
        when(aiAnalysisRepo.findByIdAndGoalId(analysisId, testGoal.getId())).thenReturn(Optional.of(testAIAnalysis));

        aiAnalysisService.deleteAIAnalysis(userId, goalId, analysisId);

        verify(aiAnalysisRepo, times(1)).delete(testAIAnalysis);
    }

    @Test
    @DisplayName("getAIAnalysis() — throws exception if analysis not found")
    void getAIAnalysis_shouldThrowIfNotFound() {
        Long userId = 1L;
        Long goalId = 1L;
        Long analysisId = 99L;

        when(goalService.findByIdAndUserId(goalId, userId)).thenReturn(testGoal);
        when(aiAnalysisRepo.findByIdAndGoalId(analysisId, testGoal.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                aiAnalysisService.getAIAnalysis(userId, analysisId, goalId)
        );
    }
}