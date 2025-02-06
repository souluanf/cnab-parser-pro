package dev.luanfernandes.controller.impl;

import dev.luanfernandes.controller.AuthController;
import dev.luanfernandes.domain.request.TokenRequest;
import dev.luanfernandes.domain.response.TokenResponse;
import dev.luanfernandes.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<TokenResponse> getToken(TokenRequest user) {
        return ResponseEntity.ok(authService.getToken(user));
    }

    @Override
    public ResponseEntity<TokenResponse> refreshToken(String refreshToken) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @Override
    public ResponseEntity<Void> logout(String refreshToken) {
        authService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }
}
