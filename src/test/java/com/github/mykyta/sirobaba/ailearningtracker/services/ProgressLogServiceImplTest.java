package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.ProgressLogNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogDetailsResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.ProgressLog;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.ProgressLogMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.ProgressLogRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.ProgressLogServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Tag("Service")
@DisplayName("Progress log service test")
@ExtendWith(MockitoExtension.class)
class ProgressLogServiceImplTest {

    @Mock
    private ProgressLogRepo progressLogRepo;

    @Mock
    private GoalService goalService;

    @Mock
    private ProgressLogMapper progressLogMapper;

    @InjectMocks
    private ProgressLogServiceImpl progressLogService;


    @Test
    @DisplayName("createProgressLog() — creates a progress log and returns DTO")
    void createProgressLog_ShouldSaveAndReturnDto() {
        Long goalId = 1L;
        Long userId = 1L;
        Goal goal = ModelUtils.createTestGoal();

        ProgressLogRequestDto requestDto = ProgressLogRequestDto.builder()
                .title("Check progress")
                .hours(1)
                .minutes(30)
                .note("Good day of learning")
                .build();

        ProgressLog savedEntity = ModelUtils.createTestProgressLog();
        ProgressLogResponseDto expectedDto = ProgressLogResponseDto.builder()
                .progressLogId(savedEntity.getId())
                .title(savedEntity.getTitle())
                .logTime(LocalDateTime.now())
                .totalMinutes(90)
                .formattedTime("1h 30m")
                .build();

        when(goalService.findByIdAndUserId(goalId, userId)).thenReturn(goal);
        when(progressLogRepo.save(any(ProgressLog.class))).thenReturn(savedEntity);
        when(progressLogMapper.progressLogToProgressLogResponseDto(savedEntity)).thenReturn(expectedDto);

        ProgressLogResponseDto actual = progressLogService.createProgressLog(goalId, requestDto, userId);

        assertThat(actual).isEqualTo(expectedDto);
        verify(progressLogRepo).save(any(ProgressLog.class));
        verify(goalService).findByIdAndUserId(goalId, userId);
        verify(progressLogMapper).progressLogToProgressLogResponseDto(savedEntity);
    }


    @Test
    @DisplayName("getLogsForGoal() — returns a page of logs")
    void getLogsForGoal_ShouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 5);
        Long goalId = 1L;
        Long userId = 1L;

        ProgressLog entity = new ProgressLog();
        entity.setId(1L);
        entity.setTitle("Study log");
        entity.setMinutesSpent(30);

        ProgressLogResponseDto dto = ProgressLogResponseDto.builder()
                .progressLogId(1L)
                .title("Study log")
                .build();

        Page<ProgressLog> mockPage = new PageImpl<>(List.of(entity));

        when(progressLogRepo.findProgressLogsByGoalIdAndUserId(pageable, goalId, userId))
                .thenReturn(mockPage);

        when(progressLogMapper.progressLogToProgressLogResponseDto(entity))
                .thenReturn(dto);

        PageResponse<ProgressLogResponseDto> result =
                progressLogService.getLogsForGoal(pageable, goalId, userId);

        assertThat(result.content().getFirst().getTitle()).isEqualTo("Study log");

        verify(progressLogRepo).findProgressLogsByGoalIdAndUserId(pageable, goalId, userId);
    }


    @Test
    @DisplayName("getProgressLogDetails() — returns log details")
    void getProgressLogDetails_ShouldReturnDto() {
        Long goalId = 1L;
        Long logId = 1L;
        Long userId = 1L;
        ProgressLog progressLog = ModelUtils.createTestProgressLog();
        ProgressLogDetailsResponseDto expected = ProgressLogDetailsResponseDto.builder()
                .progressLogId(1L)
                .title("Check my progress")
                .build();

        when(progressLogRepo.findByIdAndGoalIdAndUserId(goalId, logId, userId))
                .thenReturn(Optional.of(progressLog));
        when(progressLogMapper.progressLogToProgressLogDetailsResponseDto(progressLog))
                .thenReturn(expected);

        ProgressLogDetailsResponseDto actual = progressLogService.getProgressLogDetails(goalId, logId, userId);

        assertThat(actual).isEqualTo(expected);
        verify(progressLogRepo).findByIdAndGoalIdAndUserId(goalId, logId, userId);
        verify(progressLogMapper).progressLogToProgressLogDetailsResponseDto(progressLog);
    }


    @Test
    @DisplayName("getProgressLogDetails() — throws ProgressLogNotFoundException when not found")
    void getProgressLogDetails_ShouldThrowException_WhenNotFound() {
        Long goalId = 1L;
        Long logId = 1L;
        Long userId = 1L;

        when(progressLogRepo.findByIdAndGoalIdAndUserId(goalId, logId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> progressLogService.getProgressLogDetails(goalId, logId, userId))
                .isInstanceOf(ProgressLogNotFoundException.class);
    }


    @Test
    @DisplayName("deleteProgressLog() — deletes progress log when found")
    void deleteProgressLog_ShouldDelete_WhenFound() {
        Long goalId = 1L;
        Long logId = 1L;
        Long userId = 1L;
        ProgressLog progressLog = ModelUtils.createTestProgressLog();

        when(progressLogRepo.findByIdAndGoalIdAndUserId(goalId, logId, userId))
                .thenReturn(Optional.of(progressLog));

        progressLogService.deleteProgressLog(goalId, logId, userId);

        verify(progressLogRepo).delete(progressLog);
    }


    @Test
    @DisplayName("getProgressLogContent() — returns list of content")
    void getProgressLogContent_ShouldReturnList() {
        Long goalId = 1L;
        Long userId = 1L;
        Integer limit = 5;

        List<ProgressLogContentDto> mockList = List.of(
                ProgressLogContentDto.builder().id(1L).content("Learning Java").build()
        );

        when(progressLogRepo.findContentsByIdAndGoalIdAndUserId(eq(goalId), eq(userId), any()))
                .thenReturn(mockList);

        List<ProgressLogContentDto> result = progressLogService.getProgressLogContent(goalId, userId, limit);

        assertThat(result.getFirst().getContent()).isEqualTo("Learning Java");
        verify(progressLogRepo).findContentsByIdAndGoalIdAndUserId(eq(goalId), eq(userId), any());
    }


    @Test
    @DisplayName("getProgressLogContent() — throws ProgressLogNotFoundException when no records found")    void getProgressLogContent_ShouldThrow_WhenEmpty() {
        when(progressLogRepo.findContentsByIdAndGoalIdAndUserId(any(), any(), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> progressLogService.getProgressLogContent(1L, 1L, 3))
                .isInstanceOf(ProgressLogNotFoundException.class);
    }
}