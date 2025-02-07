package dev.luanfernandes.domain.mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import dev.luanfernandes.domain.request.TokenRequest;
import dev.luanfernandes.domain.response.TokenResponse;
import dev.luanfernandes.webclient.request.KeyCloakTokenRequest;
import dev.luanfernandes.webclient.response.KeyCloakTokenResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = SPRING)
public interface KeycloakMapper {

    TokenResponse map(KeyCloakTokenResponse value);

    KeyCloakTokenRequest map(TokenRequest tokenRequest);
}
