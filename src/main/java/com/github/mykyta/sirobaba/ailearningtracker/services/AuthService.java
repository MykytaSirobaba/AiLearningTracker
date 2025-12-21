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

    TokenResponseDto completeTwoFactorLogin(TwoFactorVerificationRequestDto request);
}

