package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user;

import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class TwoFactorActivationRequestDto {
    @ToString.Exclude
    private String code;
}
