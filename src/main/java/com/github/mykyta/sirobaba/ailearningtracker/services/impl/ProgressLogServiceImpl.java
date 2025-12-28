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
 * Provides functionality to create, retrieve, delete, and fetch progress log content.
 * Progress logs are always scoped to a specific goal and user.
 */
@Service
@Slf4j
@AllArgsConstructor
public class ProgressLogServiceImpl implements ProgressLogService {

    private final ProgressLogRepo progressLogRepo;
    private final GoalService goalService;
    private final ProgressLogMapper progressLogMapper;

    /**
     * Creates a new progress log entry for a specific goal.
     * <p>
     * Calculates the total time spent based on hours and minutes,
     * associates the log with the given goal, and persists it.
     *
     * @param goalId      identifier of the goal
     * @param progressLog DTO containing progress log data
     * @param userId      identifier of the log owner
     * @return response DTO representing the created progress log
     */
    @Override
    @Transactional
    public ProgressLogResponseDto createProgressLog(
            Long goalId,
            ProgressLogRequestDto progressLog,
            Long userId
    ) {
        log.info("Creating progress log for goalId={} userId={}", goalId, userId);

        Goal goal = goalService.findByIdAndUserId(goalId, userId);
        int time = sumHoursAndMinutesThenConvertToMinutes(
                progressLog.getHours(),
                progressLog.getMinutes()
        );

        ProgressLog progressLogEntity = ProgressLog.builder()
                .title(progressLog.getTitle())
                .logTime(LocalDateTime.now())
                .note(progressLog.getNote())
                .minutesSpent(time)
                .goal(goal)
                .build();

        ProgressLog progressLogSaved = progressLogRepo.save(progressLogEntity);
        log.info(
                "Progress log created: logId={} goalId={} userId={}",
                progressLogSaved.getId(),
                goalId,
                userId
        );

        return progressLogMapper
                .progressLogToProgressLogResponseDto(progressLogSaved);
    }

    /**
     * Retrieves a paginated list of progress logs for a specific goal.
     *
     * @param pageable pagination and sorting configuration
     * @param goalId   identifier of the goal
     * @param userId   identifier of the goal owner
     * @return paginated response containing progress log summaries
     */
    @Override
    public PageResponse<ProgressLogResponseDto> getLogsForGoal(
            Pageable pageable,
            Long goalId,
            Long userId
    ) {
        log.info("Fetching progress logs for goalId={} userId={}", goalId, userId);

        Page<ProgressLog> page =
                progressLogRepo.findProgressLogsByGoalIdAndUserId(
                        pageable,
                        goalId,
                        userId
                );

        Page<ProgressLogResponseDto> result =
                page.map(progressLogMapper::progressLogToProgressLogResponseDto);

        log.info(
                "Fetched {} progress logs for goalId={} userId={}",
                result.getTotalElements(),
                goalId,
                userId
        );

        return PageResponse.from(result);
    }

    /**
     * Retrieves detailed information for a specific progress log.
     *
     * @param goalId identifier of the goal
     * @param logId  identifier of the progress log
     * @param userId identifier of the log owner
     * @return detailed response DTO for the requested progress log
     * @throws ProgressLogNotFoundException if the progress log does not exist
     */
    @Override
    public ProgressLogDetailsResponseDto getProgressLogDetails(
            Long goalId,
            Long logId,
            Long userId
    ) {
        log.info(
                "Fetching progress log details for logId={} goalId={} userId={}",
                logId,
                goalId,
                userId
        );

        ProgressLog progressLog =
                findByIdAndGoalIdAndUserId(goalId, logId, userId);

        return progressLogMapper
                .progressLogToProgressLogDetailsResponseDto(progressLog);
    }

    /**
     * Deletes a progress log belonging to a specific goal and user.
     *
     * @param goalId identifier of the goal
     * @param logId  identifier of the progress log
     * @param userId identifier of the log owner
     * @throws ProgressLogNotFoundException if the progress log does not exist
     */
    @Override
    @Transactional
    public void deleteProgressLog(Long goalId, Long logId, Long userId) {
        log.info(
                "Deleting progress log logId={} goalId={} userId={}",
                logId,
                goalId,
                userId
        );

        ProgressLog progressLog =
                findByIdAndGoalIdAndUserId(goalId, logId, userId);

        progressLogRepo.delete(progressLog);

        log.info(
                "Progress log deleted logId={} goalId={} userId={}",
                logId,
                goalId,
                userId
        );
    }

    /**
     * Retrieves recent progress log content entries for a specific goal.
     * <p>
     * Typically used for analytics, AI context, or summaries.
     *
     * @param goalId identifier of the goal
     * @param userId identifier of the goal owner
     * @param limit  maximum number of entries to retrieve
     * @return list of progress log content DTOs
     * @throws ProgressLogNotFoundException if no progress log content is found
     */
    @Override
    public List<ProgressLogContentDto> getProgressLogContent(
            Long goalId,
            Long userId,
            Integer limit
    ) {
        log.info(
                "Fetching progress log content for goalId={} userId={} limit={}",
                goalId,
                userId,
                limit
        );

        Pageable pageable = PageRequest.of(0, limit);
        List<ProgressLogContentDto> progressLogContentDto =
                progressLogRepo.findContentsByIdAndGoalIdAndUserId(
                        goalId,
                        userId,
                        pageable
                );

        if (progressLogContentDto.isEmpty()) {
            log.warn(
                    "No progress log content found for goalId={} userId={}",
                    goalId,
                    userId
            );
            throw new ProgressLogNotFoundException(
                    String.format(
                            ErrorMessage.PROGRESS_LOGS_CONTENT_NOT_FOUND,
                            goalId,
                            userId
                    )
            );
        }

        log.info(
                "Fetched {} progress log content entries for goalId={} userId={}",
                progressLogContentDto.size(),
                goalId,
                userId
        );

        return progressLogContentDto;
    }

    /**
     * Finds a progress log by its identifier, goal identifier, and user identifier.
     *
     * @param goalId identifier of the goal
     * @param logId  identifier of the progress log
     * @param userId identifier of the log owner
     * @return progress log entity
     * @throws ProgressLogNotFoundException if the progress log does not exist
     */
    private ProgressLog findByIdAndGoalIdAndUserId(
            Long goalId,
            Long logId,
            Long userId
    ) {
        log.debug(
                "Finding progress log logId={} goalId={} userId={}",
                logId,
                goalId,
                userId
        );

        return progressLogRepo
                .findByIdAndGoalIdAndUserId(goalId, logId, userId)
                .orElseThrow(() -> {
                    log.warn(
                            "Progress log not found logId={} goalId={} userId={}",
                            logId,
                            goalId,
                            userId
                    );
                    return new ProgressLogNotFoundException(
                            String.format(
                                    ErrorMessage.PROGRESS_LOGS_NOT_FOUND,
                                    logId
                            )
                    );
                });
    }

    /**
     * Converts hours and minutes into total minutes.
     *
     * @param hours   number of hours
     * @param minutes number of minutes
     * @return total time in minutes
     */
    private int sumHoursAndMinutesThenConvertToMinutes(int hours, int minutes) {
        int totalMinutes = hours * 60 + minutes;
        log.debug(
                "Converted {} hours and {} minutes into {} total minutes",
                hours,
                minutes,
                totalMinutes
        );
        return totalMinutes;
    }
}
