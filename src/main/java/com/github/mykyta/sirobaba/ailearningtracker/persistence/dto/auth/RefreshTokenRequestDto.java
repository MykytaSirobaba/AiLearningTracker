package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

/**
 * Created by Mykyta Sirobaba on 26.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
public class RefreshTokenRequestDto {
    @NotBlank
    @Size(min = 20)
    @ToString.Exclude
    private String refreshToken;
}
