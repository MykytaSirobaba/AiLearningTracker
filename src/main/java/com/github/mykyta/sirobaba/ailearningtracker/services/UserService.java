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
     * Finds a user by email.
     *
     * @param email user email
     * @return found user entity
     * @throws UserNotFoundException if user with the given email does not exist
     */
    User findByEmail(String email);

    /**
     * Finds a user by id.
     *
     * @param id user id
     * @return found user entity
     * @throws UserNotFoundException if user with the given id does not exist
     */
    User findById(Long id);

    /**
     * Creates and persists a new user.
     *
     * @param user user entity to be created
     * @return created user entity
     */
    User createUser(User user);

    /**
     * Retrieves current user information as DTO.
     *
     * @param email user email
     * @return DTO containing current user information
     * @throws UserNotFoundException if user with the given email does not exist
     */
    CurrentUserInfoDto findCurrentUserInfoDto(String email);

    /**
     * Initializes two-factor authentication setup for the user.
     * <p>
     * Usually generates secret key and QR code for authenticator apps.
     *
     * @param userId user id
     * @return DTO containing 2FA setup data
     */
    TwoFactorSetupResponseDto setupTwoFactor(Long userId);

    /**
     * Activates two-factor authentication for the user after verification.
     *
     * @param request DTO containing verification code or related data
     * @param id user id
     */
    void activateTwoFactor(TwoFactorActivationRequestDto request, Long id);

    /**
     * Disables two-factor authentication for the user after verification.
     *
     * @param request DTO containing verification code or related data
     * @param id user id
     */
    void disable2Fa(TwoFactorActivationRequestDto request, Long id);
}
