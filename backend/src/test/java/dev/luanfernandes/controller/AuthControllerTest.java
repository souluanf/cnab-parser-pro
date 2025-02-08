package dev.luanfernandes.controller;

import static dev.luanfernandes.domain.constants.PathConstants.AUTH_LOGOUT_V1;
import static dev.luanfernandes.domain.constants.PathConstants.AUTH_REFRESH_TOKEN_V1;
import static dev.luanfernandes.domain.constants.PathConstants.AUTH_TOKEN_V1;
import static dev.luanfernandes.domain.request.TokenRequestBuilder.getTokenRequest;
import static dev.luanfernandes.domain.response.TokenResponseBuilder.getTokenResponse;
import static java.util.stream.Stream.of;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luanfernandes.config.security.WebSecurityConfig;
import dev.luanfernandes.config.web.ExceptionHandlerAdvice;
import dev.luanfernandes.controller.impl.AuthControllerImpl;
import dev.luanfernandes.domain.request.TokenRequest;
import dev.luanfernandes.service.AuthService;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@ActiveProfiles("test")
@WebMvcTest
@ContextConfiguration(classes = {AuthControllerImpl.class, WebSecurityConfig.class, ExceptionHandlerAdvice.class})
@DisplayName("Tests for AuthController")
class AuthControllerTest {

    MockMvc mvc;

    @MockitoBean
    AuthService authService;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should return token response when request is valid")
    void shouldReturnTokenResponseWhenRequestIsValid() throws Exception {
        var tokenRequest = getTokenRequest();
        var tokenResponse = getTokenResponse();
        when(authService.getToken(any(TokenRequest.class))).thenReturn(tokenResponse);

        mvc.perform(post(AUTH_TOKEN_V1)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(tokenResponse.accessToken()))
                .andExpect(jsonPath("$.expires_in").value(tokenResponse.expiresIn()))
                .andExpect(jsonPath("$.refresh_expires_in").value(tokenResponse.refreshExpiresIn()))
                .andExpect(jsonPath("$.refresh_token").value(tokenResponse.refreshToken()))
                .andExpect(jsonPath("$.token_type").value(tokenResponse.tokenType()))
                .andExpect(jsonPath("$.not-before-policy").value(tokenResponse.notBeforePolicy()));
    }

    @ParameterizedTest
    @MethodSource("invalidTokenRequestsProvider")
    @DisplayName("Should return 400 BAD REQUEST for invalid TokenRequests")
    void shouldReturnBadRequestForInvalidTokenRequests(TokenRequest request) throws Exception {
        mvc.perform(post(AUTH_TOKEN_V1)
                        .contentType(APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    static Stream<TokenRequest> invalidTokenRequestsProvider() {
        return of(
                new TokenRequest("", "password"),
                new TokenRequest("username", ""),
                new TokenRequest(null, "password"),
                new TokenRequest("username", null),
                new TokenRequest(null, null));
    }

    @Test
    @DisplayName("Should return token response when refreshToken is valid")
    void shouldReturnTokenResponseWhenRefreshTokenIsValid() throws Exception {
        var tokenResponse = getTokenResponse();
        when(authService.refreshToken(any(String.class))).thenReturn(tokenResponse);

        mvc.perform(post(AUTH_REFRESH_TOKEN_V1)
                        .contentType(APPLICATION_JSON)
                        .header("refreshToken", "valid_refresh_token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(tokenResponse.accessToken()))
                .andExpect(jsonPath("$.expires_in").value(tokenResponse.expiresIn()))
                .andExpect(jsonPath("$.refresh_expires_in").value(tokenResponse.refreshExpiresIn()))
                .andExpect(jsonPath("$.refresh_token").value(tokenResponse.refreshToken()))
                .andExpect(jsonPath("$.token_type").value(tokenResponse.tokenType()))
                .andExpect(jsonPath("$.not-before-policy").value(tokenResponse.notBeforePolicy()));
    }

    @Test
    @DisplayName("Should return 204 NO CONTENT when logout is successful")
    void shouldReturnNoContentWhenLogoutIsSuccessful() throws Exception {
        mvc.perform(post(AUTH_LOGOUT_V1)
                        .header("refreshToken", "valid_refresh_token")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(authService).logout("valid_refresh_token");
    }
}
