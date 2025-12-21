package com.github.mykyta.sirobaba.ailearningtracker.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Mykyta Sirobaba on 29.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Ai Learning Tracker Api",
                version = "v1.0",
                description = "API for managing goals and subgoals in the system, monitoring AI training",
                contact = @Contact(
                        name = "Mykyta Sirobaba",
                        email = "mykyta.sirobaba@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        )
)
public class SwaggerConfig {
}
