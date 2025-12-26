package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorActivationRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorSetupResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;

/**
 * Created by Mykyta Sirobaba on 16.08.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface UserService {

    /**
     * Method for finding a user by email.
     *
     * @param email - user email.
     * @return a user.
     * @throws UserNotFoundException if user with this email does not exist.
     */
    User findByEmail(String email);

    /**
     * Method for finding a user by id.
     *
     * @param id - user id.
     * @return a user.
     * @throws UserNotFoundException if user with this id does not exist.
     */
    User findById(Long id);

    User createUser(User user);

    /**
     * Method for retrieving current user information DTO.
     *
     * @param email - user email.
     * @return CurrentUserInfoDto with essential user data.
     * @throws UserNotFoundException if user with this email does not exist.
     */
    CurrentUserInfoDto findCurrentUserInfoDto(String email);

    TwoFactorSetupResponseDto setupTwoFactor(Long userId);

    void activateTwoFactor(TwoFactorActivationRequestDto request, Long id);

    void disable2Fa(TwoFactorActivationRequestDto request, Long id);
}
