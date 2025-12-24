package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.*;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.CurrentUserInfoDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorActivationRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.TwoFactorSetupResponseDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.repository.UserRepo;
import com.github.mykyta.sirobaba.ailearningtracker.security.totp.TotpService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service implementation for managing users.
 * Provides methods for retrieving user entities and mapping them to DTOs.
 */
@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final TotpService totpService;

    /**
     * Finds a user entity by email.
     *
     * @param email - user's email
     * @return User entity
     * @throws UserNotFoundException if no user exists with the given email
     */
    @Override
    public User findByEmail(String email) {
        log.debug("Looking for user by email: {}", email);
        return userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User with email {} not found", email);
                    return new UserNotFoundException(
                            String.format(ErrorMessage.USER_WITH_THIS_EMAIL_NOT_FOUND, email)
                    );
                });
    }

    /**
     * Finds a user entity by ID.
     *
     * @param id - user ID
     * @return User entity
     * @throws UserNotFoundException if no user exists with the given ID
     */
    @Override
    public User findById(Long id) {
        log.debug("Looking for user by ID: {}", id);
        return userRepo.findById(id)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", id);
                    return new UserNotFoundException(
                            String.format(ErrorMessage.USER_WITH_THIS_ID_NOT_FOUND, id)
                    );
                });
    }

    @Override
    public TwoFactorSetupResponseDto setupTwoFactor(Long userId) {
        User user = findById(userId);

        String secret = totpService.generateNewSecret();

        user.setTwoFactorSecret(secret);
        user.setTwoFactorSecretCreatedAt(Instant.now());
        userRepo.save(user);

        String issuer = "AI Learning Tracker";
        String qrCodeUrl = "otpauth://totp/"
                           + issuer + ":" + user.getEmail()
                           + "?secret=" + secret + "&issuer=" + issuer;

        return new TwoFactorSetupResponseDto(secret, qrCodeUrl);
    }

    @Override
    public void activateTwoFactor(TwoFactorActivationRequestDto request, Long id) {
        User user = findById(id);
        String storedSecret = user.getTwoFactorSecret();

        isStoredSecretNull(storedSecret);
        isTwoFactorEnabled(user.isTwoFactorEnabled());
        checkAndCleanExpiredSetup(user.getTwoFactorSecretCreatedAt(), user);
        isCodeValid(storedSecret, request.getCode());

        user.setTwoFactorEnabled(true);
        userRepo.save(user);
    }

    @Override
    public void disable2Fa(TwoFactorActivationRequestDto request, Long id) {
        User user = findById(id);
        if (!user.isTwoFactorEnabled()) {
            throw new TwoFactorNotEnabledException(ErrorMessage.TWO_FACTOR_NOT_ENABLED);
        }
        isCodeValid(user.getTwoFactorSecret(), request.getCode());

        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        user.setTwoFactorSecretCreatedAt(null);
        userRepo.save(user);
    }

    /**
     * Retrieves current user information as a DTO.
     *
     * @param email - user's email
     * @return CurrentUserInfoDto mapped from the User entity
     * @throws UserNotFoundException if no user exists with the given email
     */
    @Override
    public CurrentUserInfoDto findCurrentUserInfoDto(String email) {
        log.debug("Retrieving current user info DTO for email: {}", email);
        CurrentUserInfoDto dto = userMapper.toCurrentUserInfoDto(findByEmail(email));
        log.debug("CurrentUserInfoDto retrieved for email: {}", email);
        return dto;
    }

    private void isStoredSecretNull(String storedSecret) {
        if (storedSecret == null) {
            throw new UserDidNotCompleteSetup(ErrorMessage.USER_DID_NOT_COMPLETE_SETUP_STAGE);
        }
    }

    private void isTwoFactorEnabled(boolean enabled) {
        if (enabled) {
            throw new TwoFactorAlreadyEnabledException(ErrorMessage.TWO_FACTOR_ALREADY_ENABLED);
        }
    }

    private void checkAndCleanExpiredSetup(Instant expiration, User user) {
        if (totpService.isSetupExpired(expiration)) {
            user.setTwoFactorSecret(null);
            userRepo.save(user);
            throw new TwoFactorSetupExpiredException(ErrorMessage.TIME_FOR_ACTIVATION_EXPIRED);
        }
    }

    private void isCodeValid(String storedSecret, String code) {
        boolean isCodeValid = totpService.validateCode(storedSecret, code);
        if (!isCodeValid) {
            throw new Invalid2FaTokenException(ErrorMessage.INVALID_2FA_CODE);
        }
    }
}
