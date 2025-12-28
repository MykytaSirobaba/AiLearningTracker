package com.github.mykyta.sirobaba.ailearningtracker.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties related to frontend integration.
 * <p>
 * Contains frontend URLs used for redirects and two-factor authentication flow.
 * <p>
 * Created by Mykyta Sirobaba on 26.12.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@ConfigurationProperties(prefix = "app")
public class FrontendProperties {

    /**
     * Base URL of the frontend application.
     */
    private final String frontendUrl;

    /**
     * URL of the frontend page used for two-factor authentication.
     */
    private final String twoFaPageUrl;

    public FrontendProperties(String frontendUrl, String twoFaPageUrl) {
        this.frontendUrl = frontendUrl;
        this.twoFaPageUrl = twoFaPageUrl;
    }
}
