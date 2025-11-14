package com.rag.chatstorage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("ApiKeyAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-INTERNAL-KEY")
                                        .description("Provide your API key to authorize requests")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .info(new Info()
                        .title("RAG Chat Storage Service")
                        .description("""
                                Internal microservice responsible for storing and retrieving chat sessions 
                                and messages for the RAG platform. 
                                
                                All business endpoints under /api/** require authorization from the API Gateway 
                                using the internal service authentication key (X-INTERNAL-KEY).
                                
                                Swagger documentation and Actuator health endpoints are publicly available 
                                for development and observability.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sakshi Sehgal")
                        )
                );
    }
}

