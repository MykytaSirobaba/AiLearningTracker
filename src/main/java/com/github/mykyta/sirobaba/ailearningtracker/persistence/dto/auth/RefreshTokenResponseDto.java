package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 26.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class RefreshTokenResponseDto {
    @ToString.Exclude
    private String accessToken;
    @ToString.Exclude
    private String refreshToken;
    @ToString.Exclude
    private String tokenType;
    @ToString.Exclude
    private Long expiresIn;
}
