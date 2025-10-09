package com.github.mykyta.sirobaba.ailearningtracker.service.impl;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by Mykyta Sirobaba on 16.08.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}
