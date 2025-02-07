package dev.luanfernandes.service;

import dev.luanfernandes.domain.request.TokenRequest;
import dev.luanfernandes.domain.response.TokenResponse;

public interface AuthService {
    TokenResponse getToken(TokenRequest user);

    TokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}
