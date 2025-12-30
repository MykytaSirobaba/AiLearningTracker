package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.ModelUtils;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.services.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mykyta Sirobaba on 12.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser = ModelUtils.createTestUser();


    @Test
    @DisplayName("Should return user when email exists")
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        when(userRepo.findByEmail(ModelUtils.TEST_EMAIL)).thenReturn(Optional.ofNullable(testUser));

        User result = userService.findByEmail(ModelUtils.TEST_EMAIL);

        assertNotNull(result);
        assertEquals(ModelUtils.TEST_EMAIL, result.getEmail());
        verify(userRepo).findByEmail(ModelUtils.TEST_EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void findByEmail_ShouldThrowException_WhenUserNotFound() {
        when(userRepo.findByEmail(ModelUtils.TEST_EMAIL)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findByEmail(ModelUtils.TEST_EMAIL));
    }

    @Test
    @DisplayName("Should return user when id exist")
    void findById_ShouldReturnUser_WhenIdExists() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(ModelUtils.TEST_EMAIL, result.getEmail());
        assertEquals(1L, result.getId());
        assertEquals(ModelUtils.TEST_PASSWORD, result.getPassword());
        verify(userRepo).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void findById_ShouldThrowException_WhenUserNotFound() {
        when(userRepo.findById(ModelUtils.TEST_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(ModelUtils.TEST_ID));
    }
}