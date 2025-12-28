package com.github.mykyta.sirobaba.ailearningtracker.services;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.*;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface AuthService {

    /**
     * Registers a new user in the system.
     *
     * @param request DTO containing user registration data
     * @return token response with access and refresh tokens for the newly registered user
     */
    TokenResponseDto register(RegisterRequestDto request);

    /**
     * Authenticates a user using email and password credentials.
     *
     * @param request DTO containing login credentials
     * @return token response containing JWT tokens for authenticated user
     */
    LoginResultDto login(LoginRequestDto request);

    /**
     * Completes the authentication process for a user with two-factor authentication enabled.
     * <p>
     * Verifies the provided one-time code and, if valid, returns JWT access
     * and refresh tokens for the authenticated user.
     *
     * @param request DTO containing two-factor verification data
     * @return token response containing JWT access and refresh tokens
     */
    TokenResponseDto completeTwoFactorLogin(TwoFactorVerificationRequestDto request);

    /**
     * Refreshes an access token using a valid refresh token.
     * <p>
     * Generates a new access token (and optionally a new refresh token)
     * without requiring the user to re-authenticate.
     *
     * @param refreshTokenRequestDto DTO containing refresh token data
     * @return response containing a new access token and related metadata
     */
    RefreshTokenResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto);
}

