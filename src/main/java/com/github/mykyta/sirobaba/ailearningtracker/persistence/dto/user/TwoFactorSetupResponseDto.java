package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user;

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
public class TwoFactorSetupResponseDto {
    @ToString.Exclude
    private String secretKey;
    @ToString.Exclude
    private String qrCodeImageUrl;
}
