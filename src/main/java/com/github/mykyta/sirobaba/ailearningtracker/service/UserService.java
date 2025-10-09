package com.github.mykyta.sirobaba.ailearningtracker.service;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;

import java.util.Optional;

/**
 * Created by Mykyta Sirobaba on 16.08.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface UserService {
    Optional<User> findByEmail(String email);
}
