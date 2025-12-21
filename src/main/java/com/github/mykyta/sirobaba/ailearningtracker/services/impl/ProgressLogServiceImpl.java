package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
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
import com.github.mykyta.sirobaba.ailearningtracker.services.GoalService;
import com.github.mykyta.sirobaba.ailearningtracker.services.ProgressLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for managing progress logs for user goals.
 * <p>
 * Provides methods to create, retrieve, delete, and fetch content of progress logs.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ProgressLogServiceImpl implements ProgressLogService {

    private final ProgressLogRepo progressLogRepo;
    private final GoalService goalService;
    private final ProgressLogMapper progressLogMapper;

    @Override
    @Transactional
    public ProgressLogResponseDto createProgressLog(Long goalId, ProgressLogRequestDto progressLog, Long userId) {
        log.info("Creating progress log for goalId={} userId={}", goalId, userId);

        Goal goal = goalService.findByIdAndUserId(goalId, userId);
        int time = sumHoursAndMinutesThenConvertToMinutes(progressLog.getHours(), progressLog.getMinutes());

        ProgressLog progressLogEntity = ProgressLog.builder()
                .title(progressLog.getTitle())
                .logTime(LocalDateTime.now())
                .note(progressLog.getNote())
                .minutesSpent(time)
                .goal(goal)
                .build();

        ProgressLog progressLogSaved = progressLogRepo.save(progressLogEntity);
        log.info("Progress log created: logId={} goalId={} userId={}", progressLogSaved.getId(), goalId, userId);

        return progressLogMapper.progressLogToProgressLogResponseDto(progressLogSaved);
    }

    @Override
    public PageResponse<ProgressLogResponseDto> getLogsForGoal(Pageable pageable, Long goalId, Long userId) {
        log.info("Fetching progress logs for goalId={} userId={}", goalId, userId);

        Page<ProgressLog> page = progressLogRepo.findProgressLogsByGoalIdAndUserId(pageable, goalId, userId);
        Page<ProgressLogResponseDto> result = page.map(progressLogMapper::progressLogToProgressLogResponseDto);

        log.info("Fetched {} progress logs for goalId={} userId={}", result.getTotalElements(), goalId, userId);
        return PageResponse.from(result);
    }

    @Override
    public ProgressLogDetailsResponseDto getProgressLogDetails(Long goalId, Long logId, Long userId) {
        log.info("Fetching progress log details for logId={} goalId={} userId={}", logId, goalId, userId);

        ProgressLog progressLog = findByIdAndGoalIdAndUserId(goalId, logId, userId);
        return progressLogMapper.progressLogToProgressLogDetailsResponseDto(progressLog);
    }

    @Override
    @Transactional
    public void deleteProgressLog(Long goalId, Long logId, Long userId) {
        log.info("Deleting progress log logId={} goalId={} userId={}", logId, goalId, userId);

        ProgressLog progressLog = findByIdAndGoalIdAndUserId(goalId, logId, userId);
        progressLogRepo.delete(progressLog);

        log.info("Progress log deleted logId={} goalId={} userId={}", logId, goalId, userId);
    }

    @Override
    public List<ProgressLogContentDto> getProgressLogContent(Long goalId, Long userId, Integer limit) {
        log.info("Fetching progress log content for goalId={} userId={} limit={}", goalId, userId, limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<ProgressLogContentDto> progressLogContentDto =
                progressLogRepo.findContentsByIdAndGoalIdAndUserId(goalId, userId, pageable);

        if (progressLogContentDto.isEmpty()) {
            log.warn("No progress log content found for goalId={} userId={}", goalId, userId);
            throw new ProgressLogNotFoundException(
                    String.format(ErrorMessage.PROGRESS_LOGS_CONTENT_NOT_FOUND, goalId, userId)
            );
        }

        log.info("Fetched {} progress log content entries for goalId={} userId={}",
                progressLogContentDto.size(), goalId, userId);
        return progressLogContentDto;
    }

    private ProgressLog findByIdAndGoalIdAndUserId(Long goalId, Long logId, Long userId) {
        log.debug("Finding progress log logId={} goalId={} userId={}", logId, goalId, userId);

        return progressLogRepo.findByIdAndGoalIdAndUserId(goalId, logId, userId)
                .orElseThrow(() -> {
                    log.warn("Progress log not found logId={} goalId={} userId={}", logId, goalId, userId);
                    return new ProgressLogNotFoundException(
                            String.format(ErrorMessage.PROGRESS_LOGS_NOT_FOUND, logId)
                    );
                });
    }

    private int sumHoursAndMinutesThenConvertToMinutes(int hours, int minutes) {
        int totalMinutes = hours * 60 + minutes;
        log.debug("Converted {} hours and {} minutes into {} total minutes", hours, minutes, totalMinutes);
        return totalMinutes;
    }
}
