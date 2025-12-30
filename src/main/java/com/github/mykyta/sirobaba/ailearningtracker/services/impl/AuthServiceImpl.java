package com.github.mykyta.sirobaba.ailearningtracker.services.impl;

import com.github.mykyta.sirobaba.ailearningtracker.constants.ErrorMessage;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.InvalidRefreshTokenException;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserHasAlreadyRegistered;
import com.github.mykyta.sirobaba.ailearningtracker.exceptions.exceptions.UserNotFoundException;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.*;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.User;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.entity.enums.Role;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.mapper.UserMapper;
import com.github.mykyta.sirobaba.ailearningtracker.security.jwt.JwtTool;
import com.github.mykyta.sirobaba.ailearningtracker.security.totp.TotpService;
import com.github.mykyta.sirobaba.ailearningtracker.services.AuthService;
import com.github.mykyta.sirobaba.ailearningtracker.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service implementation for handling user authentication and registration.
 * <p>
 * Provides functionality to register new users, login existing users,
 * and generate JWT tokens.
 */
@Slf4j
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TotpService totpService;

    /**
     * Registers a new user with the provided registration data.
     *
     * @param request registration request DTO containing username, email, and password
     * @return token response DTO with access and refresh tokens
     * @throws UserHasAlreadyRegistered if email or username already exists
     */
    @Override
    public TokenResponseDto register(RegisterRequestDto request) {
        log.info("Registering new user: email={}, username={}", request.getEmail(), request.getUsername());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider("LOCAL")
                .role(Role.USER)
                .refreshTokenKey(UUID.randomUUID().toString())
                .build();

        User savedUser = userService.createUser(user);

        log.info("User registered successfully: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        return buildTokenResponse(savedUser);
    }

    /**
     * Authenticates a user using email and password and returns a JWT token response.
     *
     * @param request login request DTO containing email and password
     * @return login result DTO with access and refresh tokens or 2fa token
     * @throws UserNotFoundException if the user with the given email does not exist
     */
    @Override
    public LoginResultDto login(LoginRequestDto request) {
        log.info("Attempting login for email={}", request.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.findByEmail(request.getEmail());
        log.debug("User with email={} found", user.getEmail());

        if (user.isTwoFactorEnabled()) {
            log.info("2FA enabled for user id={}, requiring second step.", user.getId());

            String twoFactorToken = jwtTool.generate2FaToken(user);

            return LoginResultDto.builder()
                    .twoFactorRequired(true)
                    .twoFactorToken(twoFactorToken)
                    .build();
        }

        log.info("Login successful (no 2FA required) for user id={}", user.getId());
        TokenResponseDto tokens = buildTokenResponse(user);
        log.debug("Tokens for user id={}", user.getId());

        return LoginResultDto.builder()
                .tokens(tokens)
                .twoFactorRequired(false)
                .build();
    }

    /**
     * Completes the authentication process for a user with two-factor authentication enabled.
     * <p>
     * Validates the provided one-time password (OTP) using TOTP
     * and, if successful, generates JWT access and refresh tokens.
     *
     * @param request DTO containing the temporary 2FA token and verification code
     * @return token response DTO containing access and refresh tokens
     * @throws BadCredentialsException if the provided 2FA code is invalid
     */
    @Override
    public TokenResponseDto completeTwoFactorLogin(TwoFactorVerificationRequestDto request) {
        log.info("The beginning of the end of two-factor authentication");
        Long userId = jwtTool.getUserIdFrom2FaToken(request.getTwoFactorToken());

        User user = userService.findById(userId);

        boolean isCodeValid = totpService.validateCode(
                user.getTwoFactorSecret(),
                request.getCode()
        );

        if (!isCodeValid) {
            log.warn("Two-factor authentication failed for user id={}", userId);
            throw new BadCredentialsException(ErrorMessage.INVALID_2FA_CODE);
        }

        log.info("Two-factor authentication successful for user id={}", userId);
        return buildTokenResponse(user);
    }

    /**
     * Builds a token response object for a given user.
     *
     * @param user the authenticated or newly registered user
     * @return token response DTO containing access token, refresh token,
     * token type, expiration, and user profile data
     */
    private TokenResponseDto buildTokenResponse(User user) {
        log.debug("Generating JWT tokens for user id={}", user.getId());

        final String accessToken = jwtTool.generateAccessToken(user);
        final String refreshToken = jwtTool.generateRefreshToken(user);

        TokenResponseDto response = TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTool.getExpirationDate(accessToken))
                .user(userMapper.toUserResponseDto(user))
                .build();

        log.debug("Token response built successfully for user id={}", user.getId());
        return response;
    }


    /**
     * Refreshes an access token using a valid refresh token.
     * <p>
     * Validates the refresh token, extracts the associated user,
     * and generates a new access token without requiring re-authentication.
     *
     * @param refreshTokenRequestDto DTO containing the refresh token
     * @return response DTO containing a new access token and related metadata
     * @throws InvalidRefreshTokenException if the refresh token is invalid or expired
     */
    @Override
    public RefreshTokenResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {
        log.info("Refresh JWT token");
        final String refreshToken = refreshTokenRequestDto.getRefreshToken();
        final User user = userService.findByEmail(jwtTool.extractEmail(refreshToken));
        log.debug("Find user with email={}", user.getEmail());
        if (jwtTool.validateRefreshToken(refreshToken, user)) {
            log.info("Successfully refreshed token for user id={}", user.getId());
            return RefreshTokenResponseDto.builder()
                    .accessToken(jwtTool.generateAccessToken(user))
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtTool.getExpirationDate(refreshToken))
                    .build();
        }
        throw new InvalidRefreshTokenException(String.format(ErrorMessage.INVALID_REFRESH_TOKEN, refreshToken));
    }
}
