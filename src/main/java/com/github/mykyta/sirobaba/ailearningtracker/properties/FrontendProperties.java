package com.github.mykyta.sirobaba.ailearningtracker.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by Mykyta Sirobaba on 26.12.2025.
 * email mykyta.sirobaba@gmail.com
 */

@Data
@ConfigurationProperties(prefix = "app")
public class FrontendProperties {

    private final String frontendUrl;
    private final String twoFaPageUrl;

    public FrontendProperties(String frontendUrl, String twoFaPageUrl) {
        this.frontendUrl = frontendUrl;
        this.twoFaPageUrl = twoFaPageUrl;
    }
}
