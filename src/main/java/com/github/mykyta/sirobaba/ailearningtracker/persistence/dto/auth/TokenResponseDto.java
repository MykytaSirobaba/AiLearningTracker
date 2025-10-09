package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class TokenResponseDto {
    String accessToken;
    String refreshToken;
    String tokenType;
    private long expiresIn;
    private UserResponseDto user;
}
