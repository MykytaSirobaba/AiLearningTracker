package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.Subgoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Subgoal} entities.
 * <p>
 * Provides CRUD operations and custom queries for retrieving subgoals
 * associated with specific users. Utilizes Spring Data JPA for database access.
 * </p>
 *
 * <p>Created by Mykyta Sirobaba on 15.08.2025.</p>
 * <p>Email: mykyta.sirobaba@gmail.com</p>
 */
@Repository
public interface SubgoalRepo extends JpaRepository<Subgoal, Long> {

    /**
     * Finds a {@link Subgoal} entity by its ID and the owner's user ID.
     * <p>
     * Ensures that the subgoal belongs to the specified user.
     * </p>
     *
     * @param subGoalId the ID of the subgoal
     * @param ownerId   the ID of the user who owns the parent goal
     * @return an {@link Optional} containing the {@link Subgoal} if found, or empty if not found
     */
    @Query("""
    SELECT s FROM Subgoal s WHERE s.id = :subGoalId AND s.goal.user.id = :ownerId
    """)
    Optional<Subgoal> findBySubgoalAndUserId(Long subGoalId, Long ownerId);
}
