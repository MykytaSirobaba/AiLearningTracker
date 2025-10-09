package com.github.mykyta.sirobaba.ailearningtracker.persistence.repository;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by Mykyta Sirobaba on 15.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
