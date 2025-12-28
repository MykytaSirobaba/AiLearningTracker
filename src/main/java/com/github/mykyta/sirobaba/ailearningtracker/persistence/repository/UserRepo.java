package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link User} entities.
 * <p>
 * Provides CRUD operations and custom queries for retrieving users
 * based on unique attributes such as email and username.
 * Utilizes Spring Data JPA for database access.
 * </p>
 *
 * <p>Created by Mykyta Sirobaba on 15.08.2025.</p>
 * <p>Email: mykyta.sirobaba@gmail.com</p>
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Finds a {@link User} entity by its email.
     *
     * @param email the email of the user
     * @return an {@link Optional} containing the {@link User} if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a {@link User} with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a {@link User} with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
