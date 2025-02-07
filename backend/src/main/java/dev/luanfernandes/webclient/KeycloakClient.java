package dev.luanfernandes.webclient;

import static dev.luanfernandes.domain.constants.PathWebClientConstants.LOGOUT;
import static dev.luanfernandes.domain.constants.PathWebClientConstants.TOKEN;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import dev.luanfernandes.webclient.request.KeyCloakTokenRequest;
import dev.luanfernandes.webclient.response.KeyCloakTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class KeycloakClient {

    @Value("${spring.security.oauth2.resource-server.jwt.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.resource-server.jwt.realm}")
    private String realm;

    private static final String CLIENT_ID = "client_id";
    private static final String REFRESH_TOKEN = "refresh_token";

    private final RestClient keycloak;

    public KeyCloakTokenResponse getToken(KeyCloakTokenRequest tokenRequest) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", tokenRequest.username());
        formData.add("password", tokenRequest.password());
        formData.add(CLIENT_ID, clientId);
        formData.add("grant_type", "password");
        return keycloak.post()
                .uri(TOKEN, realm)
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .body(formData)
                .retrieve()
                .body(KeyCloakTokenResponse.class);
    }

    public KeyCloakTokenResponse refreshToken(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(REFRESH_TOKEN, refreshToken);
        formData.add(CLIENT_ID, clientId);
        formData.add("grant_type", REFRESH_TOKEN);
        return keycloak.post()
                .uri(TOKEN, realm)
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .body(formData)
                .retrieve()
                .body(KeyCloakTokenResponse.class);
    }

    public void logout(String refreshToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(REFRESH_TOKEN, refreshToken);
        formData.add(CLIENT_ID, clientId);
        keycloak.post()
                .uri(LOGOUT, realm)
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .body(formData)
                .retrieve()
                .toBodilessEntity();
    }
}
