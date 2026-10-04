package com.streamingsystem.cloudservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Streaming Api Specification",
                description = "Api documentation for Streaming",
                version = "1.0",
                contact = @Contact(
                        name = "Viet Hoang",
                        email = "viethoang@gmail.com"
                ),
                license = @License(
                        name = "MIT License"
                )
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                ),
        }
)
public class OpenApiConfig {
}
