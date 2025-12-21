package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.ProgressLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link ProgressLog} entities.
 * <p>
 * Provides CRUD operations and custom queries for retrieving progress logs
 * associated with specific goals and users. Supports pagination, content-only
 * DTO retrieval, and fetching individual logs by ID.
 * </p>
 *
 * <p>Created by Mykyta Sirobaba on 15.08.2025.</p>
 * <p>Email: mykyta.sirobaba@gmail.com</p>
 */
@Repository
public interface ProgressLogRepo extends JpaRepository<ProgressLog, Long> {

    /**
     * Retrieves a paginated list of {@link ProgressLog} entries for a specific goal and user,
     * ordered by the log time in descending order (most recent first).
     *
     * @param pageable pagination information
     * @param goalId   the ID of the goal
     * @param userId   the ID of the user who owns the goal
     * @return a {@link Page} of {@link ProgressLog} entities
     */
    @Query("SELECT p FROM ProgressLog p WHERE p.goal.id = :goalId AND p.goal.user.id = :userId ORDER BY p.logTime DESC")
    Page<ProgressLog> findProgressLogsByGoalIdAndUserId(Pageable pageable, @Param("goalId") Long goalId, @Param("userId") Long userId);

    /**
     * Retrieves a list of progress log contents as {@link ProgressLogContentDto} for a specific goal and user.
     * <p>
     * Only the ID and note of each progress log are returned. Results are ordered by log time descending.
     * Pagination is applied via the {@link Pageable} parameter.
     * </p>
     *
     * @param goalId   the ID of the goal
     * @param userId   the ID of the user who owns the goal
     * @param pageable pagination information
     * @return a {@link List} of {@link ProgressLogContentDto} containing progress log ID and note
     */
    @Query("""
                SELECT new com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.progresslog.ProgressLogContentDto(pl.id, pl.note)
                FROM ProgressLog pl
                WHERE pl.goal.id = :goalId AND pl.goal.user.id = :userId
                ORDER BY pl.logTime DESC
            """)
    List<ProgressLogContentDto> findContentsByIdAndGoalIdAndUserId(
            @Param("goalId") Long goalId,
            @Param("userId") Long userId,
            Pageable pageable);

    /**
     * Finds a single {@link ProgressLog} by its ID, the associated goal ID, and the user ID.
     *
     * @param logId   the ID of the progress log
     * @param goalId  the ID of the goal
     * @param userId  the ID of the user who owns the goal
     * @return an {@link Optional} containing the {@link ProgressLog} if found, or empty if not found
     */
    @Query("""
                SELECT p FROM ProgressLog p
                WHERE p.id = :logId AND p.goal.id = :goalId AND p.goal.user.id = :userId
            """)
    Optional<ProgressLog> findByIdAndGoalIdAndUserId(@Param("goalId") Long goalId, @Param("logId") Long logId, @Param("userId") Long userId);
}
