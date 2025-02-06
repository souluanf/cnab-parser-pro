package dev.luanfernandes.config.rest;

import static org.springframework.web.client.RestClient.builder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${spring.security.oauth2.resource-server.jwt.host}")
    private String keycloakUri;

    @Bean
    public RestClient keycloak() {
        return builder().baseUrl(keycloakUri).build();
    }
}
