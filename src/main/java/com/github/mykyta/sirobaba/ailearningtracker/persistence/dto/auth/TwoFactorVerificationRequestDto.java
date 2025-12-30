package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class TwoFactorVerificationRequestDto {
    @ToString.Exclude
    private String twoFactorToken;
    @ToString.Exclude
    private String code;
}
