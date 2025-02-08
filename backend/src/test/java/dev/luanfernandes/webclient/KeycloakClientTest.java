package dev.luanfernandes.webclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import dev.luanfernandes.webclient.request.KeyCloakTokenRequest;
import dev.luanfernandes.webclient.response.KeyCloakTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class KeycloakClientTest {

    @InjectMocks
    private KeycloakClient keycloakClient;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @BeforeEach
    void setUp() {
        lenient().doReturn(requestBodyUriSpec).when(restClient).post();
        lenient().doReturn(requestBodyUriSpec).when(restClient).put();
        lenient().doReturn(requestHeadersUriSpec).when(restClient).get();
    }

    @Test
    @DisplayName("Should retrieve token successfully")
    void shouldRetrieveTokenSuccessfully() {
        KeyCloakTokenRequest tokenRequest = KeyCloakTokenRequest.builder()
                .username("username")
                .password("password")
                .build();

        String accessToken = "Bearer token";
        KeyCloakTokenResponse tokenResponse = KeyCloakTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(300)
                .refreshExpiresIn(1800)
                .refreshToken("refresh_token")
                .tokenType("Bearer")
                .notBeforePolicy(0)
                .sessionState("e3c5e649-4be1-421d-a5bc-20a12a6a4fb2")
                .scope("email profile")
                .build();

        ReflectionTestUtils.setField(keycloakClient, "clientId", "test-client-id");
        ReflectionTestUtils.setField(keycloakClient, "realm", "test-realm");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", tokenRequest.username());
        formData.add("password", tokenRequest.password());
        formData.add("client_id", "test-client-id");
        formData.add("grant_type", "password");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/realms/{realm}/protocol/openid-connect/token", "test-realm"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.body(formData)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(KeyCloakTokenResponse.class)).thenReturn(tokenResponse);

        KeyCloakTokenResponse actualResponse = keycloakClient.getToken(tokenRequest);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.accessToken()).isEqualTo(accessToken);

        verify(requestBodyUriSpec, times(1)).uri("/realms/{realm}/protocol/openid-connect/token", "test-realm");
        verify(requestBodySpec, times(1)).header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);
        verify(requestBodySpec, times(1)).body(formData);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(KeyCloakTokenResponse.class);
    }

    @Test
    @DisplayName("Should retrieve refresh token successfully")
    void shouldRetrieveRefreshTokenSuccessfully() {
        String refreshToken = "valid_refresh_token";
        String accessToken = "Bearer new_token";
        KeyCloakTokenResponse tokenResponse = KeyCloakTokenResponse.builder()
                .accessToken(accessToken)
                .expiresIn(300)
                .refreshExpiresIn(1800)
                .refreshToken("new_refresh_token")
                .tokenType("Bearer")
                .notBeforePolicy(0)
                .sessionState("session-id")
                .scope("email profile")
                .build();

        ReflectionTestUtils.setField(keycloakClient, "clientId", "test-client-id");
        ReflectionTestUtils.setField(keycloakClient, "realm", "test-realm");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("refresh_token", refreshToken);
        formData.add("client_id", "test-client-id");
        formData.add("grant_type", "refresh_token");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/realms/{realm}/protocol/openid-connect/token", "test-realm"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.body(formData)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(KeyCloakTokenResponse.class)).thenReturn(tokenResponse);

        KeyCloakTokenResponse actualResponse = keycloakClient.refreshToken(refreshToken);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.accessToken()).isEqualTo(accessToken);

        verify(requestBodyUriSpec, times(1)).uri("/realms/{realm}/protocol/openid-connect/token", "test-realm");
        verify(requestBodySpec, times(1)).header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);
        verify(requestBodySpec, times(1)).body(formData);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).body(KeyCloakTokenResponse.class);
    }

    @Test
    @DisplayName("Should perform logout successfully")
    void shouldPerformLogoutSuccessfully() {
        String refreshToken = "valid_refresh_token";

        ReflectionTestUtils.setField(keycloakClient, "clientId", "test-client-id");
        ReflectionTestUtils.setField(keycloakClient, "realm", "test-realm");

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("refresh_token", refreshToken);
        formData.add("client_id", "test-client-id");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/realms/{realm}/protocol/openid-connect/logout", "test-realm"))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.body(formData)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        keycloakClient.logout(refreshToken);

        verify(requestBodyUriSpec, times(1)).uri("/realms/{realm}/protocol/openid-connect/logout", "test-realm");
        verify(requestBodySpec, times(1)).header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);
        verify(requestBodySpec, times(1)).body(formData);
        verify(requestBodySpec, times(1)).retrieve();
        verify(responseSpec, times(1)).toBodilessEntity();
    }
}
