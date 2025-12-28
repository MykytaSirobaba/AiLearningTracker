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
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service implementation for managing users.
 * <p>
 * Handles user retrieval, creation, and two-factor authentication lifecycle
 * including setup, activation, and disabling.
 */
@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final TotpService totpService;

    /**
     * Creates a new user if email and username are unique.
     *
     * @param user user entity to be created
     * @return persisted user entity
     * @throws UserHasAlreadyRegistered if email or username is already taken
     */
    @Override
    @Transactional
    public User createUser(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            log.warn("Registration failed: email already registered: {}", user.getEmail());
            throw new UserHasAlreadyRegistered(ErrorMessage.EMAIL_ALREADY_REGISTERED);
        }

        if (userRepo.existsByUsername(user.getUsername())) {
            log.warn("Registration failed: username already registered: {}", user.getUsername());
            throw new UserHasAlreadyRegistered(ErrorMessage.USER_ALREADY_REGISTERED_WITH_THIS_NAME);
        }

        return userRepo.save(user);
    }

    /**
     * Initializes two-factor authentication setup for the user.
     * <p>
     * Generates a new TOTP secret, stores it temporarily,
     * and returns QR code URL for authenticator apps.
     *
     * @param userId user id
     * @return DTO containing secret key and QR code URL
     */
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

    /**
     * Activates two-factor authentication for the user.
     * <p>
     * Verifies provided TOTP code, checks setup expiration,
     * and enables 2FA if validation succeeds.
     *
     * @param request DTO containing verification code
     * @param id      user id
     * @throws UserDidNotCompleteSetup          if setup stage was not completed
     * @throws TwoFactorAlreadyEnabledException if 2FA is already enabled
     * @throws TwoFactorSetupExpiredException   if activation time has expired
     * @throws Invalid2FaTokenException         if provided code is invalid
     */
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

    /**
     * Disables two-factor authentication for the user.
     * <p>
     * Requires valid TOTP code to confirm user identity.
     *
     * @param request DTO containing verification code
     * @param id      user id
     * @throws TwoFactorNotEnabledException if 2FA is not enabled
     * @throws Invalid2FaTokenException     if provided code is invalid
     */
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

    /**
     * Checks whether stored 2FA secret exists.
     *
     * @param storedSecret stored TOTP secret
     * @throws UserDidNotCompleteSetup if secret is missing
     */
    private void isStoredSecretNull(String storedSecret) {
        if (storedSecret == null) {
            throw new UserDidNotCompleteSetup(ErrorMessage.USER_DID_NOT_COMPLETE_SETUP_STAGE);
        }
    }

    /**
     * Checks whether two-factor authentication is already enabled.
     *
     * @param enabled current 2FA status
     * @throws TwoFactorAlreadyEnabledException if 2FA is already enabled
     */
    private void isTwoFactorEnabled(boolean enabled) {
        if (enabled) {
            throw new TwoFactorAlreadyEnabledException(ErrorMessage.TWO_FACTOR_ALREADY_ENABLED);
        }
    }

    /**
     * Validates setup expiration and cleans expired setup data.
     *
     * @param expiration secret creation timestamp
     * @param user       user entity
     * @throws TwoFactorSetupExpiredException if setup has expired
     */
    private void checkAndCleanExpiredSetup(Instant expiration, User user) {
        if (totpService.isSetupExpired(expiration)) {
            user.setTwoFactorSecret(null);
            userRepo.save(user);
            throw new TwoFactorSetupExpiredException(ErrorMessage.TIME_FOR_ACTIVATION_EXPIRED);
        }
    }

    /**
     * Validates provided TOTP code against stored secret.
     *
     * @param storedSecret stored TOTP secret
     * @param code         verification code provided by user
     * @throws Invalid2FaTokenException if code validation fails
     */
    private void isCodeValid(String storedSecret, String code) {
        boolean isCodeValid = totpService.validateCode(storedSecret, code);
        if (!isCodeValid) {
            throw new Invalid2FaTokenException(ErrorMessage.INVALID_2FA_CODE);
        }
    }
}
