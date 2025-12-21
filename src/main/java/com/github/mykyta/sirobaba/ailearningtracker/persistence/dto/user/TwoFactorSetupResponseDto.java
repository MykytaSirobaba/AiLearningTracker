package com.github.mykyta.sirobaba.ailearningtracker.persistence.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Created by Mykyta Sirobaba on 18.11.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@Builder
@AllArgsConstructor
public class TwoFactorSetupResponseDto {
    private String secretKey;
    private String qrCodeImageUrl;
}
