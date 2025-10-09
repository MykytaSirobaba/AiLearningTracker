package com.github.mykyta.sirobaba.ailearningtracker.service;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.LoginRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.RegisterRequestDto;
import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth.TokenResponseDto;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
public interface AuthService {
    TokenResponseDto login(LoginRequestDto request);
    TokenResponseDto register(RegisterRequestDto request);
}
