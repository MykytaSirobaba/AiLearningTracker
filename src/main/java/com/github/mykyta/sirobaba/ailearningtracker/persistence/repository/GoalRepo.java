package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Goal} entities.
 * <p>
 * Provides CRUD operations and custom queries for retrieving goals, including summaries
 * with the number of associated subgoals and filtering by completion status.
 * Utilizes Spring Data JPA to interact with the underlying database.
 * </p>
 *
 * <p>Created by Mykyta Sirobaba on 15.08.2025.</p>
 * <p>Email: mykyta.sirobaba@gmail.com</p>
 */
@Repository
public interface GoalRepo extends JpaRepository<Goal, Long> {

    /**
     * Retrieves a paginated list of non-completed goals for a specific user,
     * including the count of associated subgoals.
     *
     * @param pageable pagination information
     * @param ownerId  the ID of the user who owns the goals
     * @return a {@link Page} of {@link GoalSummaryDto} representing non-completed goals
     */
    @Query("""
            SELECT new com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto(
                g.id,
                g.title,
                g.description,
                g.deadline,
                COUNT(s.id),
                g.completed
            )
            FROM Goal g
            LEFT JOIN g.subgoals s
            WHERE g.completed != true AND g.user.id = :ownerId
            GROUP BY g.id, g.title, g.description, g.deadline, g.completed
            """)
    Page<GoalSummaryDto> findAllNonCompletedGoalsWithSubCount(Pageable pageable, @Param("ownerId") Long ownerId);

    /**
     * Retrieves a paginated list of completed goals for a specific user,
     * including the count of associated subgoals.
     *
     * @param pageable pagination information
     * @param ownerId  the ID of the user who owns the goals
     * @return a {@link Page} of {@link GoalSummaryDto} representing completed goals
     */
    @Query("""
            SELECT new com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.goal.GoalSummaryDto(
                g.id,
                g.title,
                g.description,
                g.deadline,
                COUNT(s.id),
                g.completed
            )
            FROM Goal g
            LEFT JOIN g.subgoals s
            WHERE g.completed = true AND g.user.id = :ownerId
            GROUP BY g.id, g.title, g.description, g.deadline, g.completed
            """)
    Page<GoalSummaryDto> findAllCompletedGoalsWithSubCount(Pageable pageable, @Param("ownerId") Long ownerId);

    /**
     * Finds a {@link Goal} entity by its ID and the owner's user ID.
     * <p>
     * Uses {@link EntityGraph} to fetch associated subgoals eagerly to avoid N+1 query problems.
     * </p>
     *
     * @param goalId the ID of the goal
     * @param userId the ID of the user who owns the goal
     * @return an {@link Optional} containing the {@link Goal} if found, or empty if not found
     */
    @EntityGraph(attributePaths = {"subgoals"})
    @Query("""
                SELECT g FROM Goal g WHERE g.id = :goalId AND g.user.id = :userId
            """)
    Optional<Goal> findByGoalIdAndOwnerId(Long goalId, @Param("userId") Long userId);
}
