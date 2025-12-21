package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.AIAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link AIAnalysis} entities.
 * <p>
 * Provides CRUD operations and custom queries for retrieving AI analyses associated with specific goals and users.
 * Uses Spring Data JPA to interact with the underlying database.
 * </p>
 *
 * <p>Created by Mykyta Sirobaba on 15.08.2025.</p>
 * <p>Email: mykyta.sirobaba@gmail.com</p>
 */
@Repository
public interface AIAnalysisRepo extends JpaRepository<AIAnalysis, Long> {

    /**
     * Finds an AIAnalysis entity by its ID and the associated Goal ID.
     *
     * @param analysisId the ID of the AIAnalysis
     * @param goalId     the ID of the associated Goal
     * @return an {@link Optional} containing the AIAnalysis if found, or empty if not found
     */
    @Query("SELECT a FROM AIAnalysis a WHERE a.id = :analysisId AND a.goal.id = :goalId")
    Optional<AIAnalysis> findByIdAndGoalId(@Param("analysisId") Long analysisId, @Param("goalId") Long goalId);

    /**
     * Retrieves a paginated list of {@link AIAnalysisResponseDto} for a given user and goal.
     * <p>
     * The DTO contains only selected fields (id, title, createdAt) to reduce data transfer.
     * Results are ordered by creation date in descending order (latest first).
     * </p>
     *
     * @param userId   the ID of the user who owns the goal
     * @param goalId   the ID of the goal
     * @param pageable the pagination information
     * @return a {@link Page} of {@link AIAnalysisResponseDto} containing AI analyses for the specified user and goal
     */
    @Query("""
    SELECT new com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.aianalysis.AIAnalysisResponseDto(
        a.id,
        a.title,
        a.createdAt
    )
    FROM AIAnalysis a
    JOIN a.goal g
    WHERE g.id = :goalId AND g.user.id = :userId
    ORDER BY a.createdAt DESC
    """)
    Page<AIAnalysisResponseDto> findByIdAndUserIdAndGoalId(
            @Param("userId") Long userId,
            @Param("goalId") Long goalId,
            Pageable pageable
    );

}
