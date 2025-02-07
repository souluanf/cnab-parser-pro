package dev.luanfernandes.webclient.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record KeyCloakTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("refresh_expires_in") Integer refreshExpiresIn,
        @JsonProperty("not-before-policy") Integer notBeforePolicy,
        @JsonProperty("scope") String scope,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("session_state") String sessionState,
        @JsonProperty("expires_in") Integer expiresIn) {}
