package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogDetailsResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.tool.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Mykyta Sirobaba on 30.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface ProgressLogService {

    /**
     * Method for creating a new progress log for a goal.
     *
     * @param goalId      - id of the goal.
     * @param progressLog - DTO containing progress log creation data.
     * @param userId      - id of the user who owns the goal.
     * @return ProgressLogResponseDto containing created progress log information.
     */
    ProgressLogResponseDto createProgressLog(Long goalId, ProgressLogRequestDto progressLog, Long userId);

    /**
     * Method for retrieving paginated progress logs for a specific goal.
     *
     * @param pageable - pagination parameters.
     * @param goalId   - id of the goal.
     * @param userId   - id of the user who owns the goal.
     * @return PageResponse containing paginated progress logs.
     */
    PageResponse<ProgressLogResponseDto> getLogsForGoal(Pageable pageable, Long goalId, Long userId);

    /**
     * Method for retrieving detailed information about a specific progress log.
     *
     * @param goalId - id of the goal.
     * @param logId  - id of the progress log.
     * @param userId - id of the user who owns the progress log.
     * @return ProgressLogDetailsResponseDto with details of the requested progress log.
     */
    ProgressLogDetailsResponseDto getProgressLogDetails(Long goalId, Long logId, Long userId);

    /**
     * Method for retrieving content of progress logs for the given goal.
     *
     * @param goalId - id of the goal.
     * @param userId - id of the user.
     * @param limit  - maximum number of logs to return.
     * @return list of ProgressLogContentDto.
     */
    List<ProgressLogContentDto> getProgressLogContent(Long goalId, Long userId, Integer limit);

    /**
     * Method for deleting a progress log.
     *
     * @param goalId - id of the goal.
     * @param logId  - id of the log.
     * @param userId - id of the user who owns the log.
     */
    void deleteProgressLog(Long goalId, Long logId, Long userId);
}


