package com.github.mykyta.sirobaba.ailearningtracker.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by Mykyta Sirobaba on 10.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.ai")
@EnableConfigurationProperties(AiTemplateConfig.class)
public class AiTemplateConfig {
    private Map<String, String> templates;

    public String getTemplate(String templateName) {
        if (!templates.containsKey(templateName)) {
            throw new IllegalArgumentException("Template not found: " + templateName);
        }
        return templates.get(templateName);
    }
}
