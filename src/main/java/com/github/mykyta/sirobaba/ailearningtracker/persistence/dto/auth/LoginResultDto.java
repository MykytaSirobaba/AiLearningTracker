package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Builder
@AllArgsConstructor
public class LoginResultDto {
    @ToString.Exclude
    private TokenResponseDto tokens;
    @ToString.Exclude
    private String twoFactorToken;
    private boolean twoFactorRequired;
}
