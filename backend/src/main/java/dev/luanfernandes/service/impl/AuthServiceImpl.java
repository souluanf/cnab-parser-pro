package dev.luanfernandes.service.impl;

import dev.luanfernandes.domain.mapper.KeycloakMapper;
import dev.luanfernandes.domain.request.TokenRequest;
import dev.luanfernandes.domain.response.TokenResponse;
import dev.luanfernandes.service.AuthService;
import dev.luanfernandes.webclient.KeycloakClient;
import dev.luanfernandes.webclient.request.KeyCloakTokenRequest;
import dev.luanfernandes.webclient.response.KeyCloakTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KeycloakClient webClient;
    private final KeycloakMapper keycloakMapper;

    @Override
    public TokenResponse getToken(TokenRequest tokenRequest) {
        KeyCloakTokenRequest keyCloakTokenRequest = keycloakMapper.map(tokenRequest);

        KeyCloakTokenResponse keyCloakTokenResponse = webClient.getToken(keyCloakTokenRequest);

        return keycloakMapper.map(keyCloakTokenResponse);
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return keycloakMapper.map(webClient.refreshToken(refreshToken));
    }

    @Override
    public void logout(String refreshToken) {
        webClient.logout(refreshToken);
    }
}
