package dev.luanfernandes.domain.response;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenResponseBuilder {

    public static TokenResponse getTokenResponse() {
        return TokenResponse.builder()
                .accessToken("access_token")
                .expiresIn(300)
                .refreshExpiresIn(1800)
                .refreshToken("refresh_token")
                .tokenType("Bearer")
                .notBeforePolicy(0)
                .sessionState("e3c5e649-4be1-421d-a5bc-20a12a6a4fb2")
                .scope("email profile")
                .build();
    }
}
