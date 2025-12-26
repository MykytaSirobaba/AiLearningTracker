package com.github.mykyta.sirobaba.ailearningtracker;

import com.github.mykyta.sirobaba.ailearningtracker.properties.FrontendProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(FrontendProperties.class)
public class AiLearningTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearningTrackerApplication.class, args);
    }

}
