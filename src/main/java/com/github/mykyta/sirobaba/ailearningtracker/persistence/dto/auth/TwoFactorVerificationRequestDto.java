package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class TwoFactorVerificationRequestDto {
    private String twoFactorToken;
    private String code;
}
