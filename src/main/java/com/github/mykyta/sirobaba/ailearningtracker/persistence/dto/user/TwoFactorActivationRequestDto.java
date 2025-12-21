package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user;

import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class TwoFactorActivationRequestDto {
    private String code;
}
