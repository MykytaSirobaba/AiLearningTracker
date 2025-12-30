package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class TokenResponseDto {
    @ToString.Exclude
    private String accessToken;
    @ToString.Exclude
    private String refreshToken;
    @ToString.Exclude
    private String tokenType;
    @ToString.Exclude
    private Long expiresIn;
    private UserResponseDto user;
}
