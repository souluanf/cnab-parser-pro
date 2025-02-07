package dev.luanfernandes.webclient.request;

import lombok.Builder;
import org.springframework.validation.annotation.Validated;

@Builder
@Validated
public record KeyCloakTokenRequest(String username, String password) {}
