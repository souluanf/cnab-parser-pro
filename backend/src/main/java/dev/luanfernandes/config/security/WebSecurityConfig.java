package dev.luanfernandes.config.security;

import static dev.luanfernandes.domain.constants.PathConstants.AUTH_V1;
import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTIONS_GROUPED_V1;
import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTIONS_V1;
import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTION_CPF_V1;
import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTION_PROCESS_FILE_V1;
import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTION_STORE_BALANCE_V1;
import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTION_STORE_NAME_V1;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final String ADMIN_ROLE = "ADMIN";

    private static final RequestMatcher[] ALLOWED_PATHS = {
        antMatcher("/actuator/**"),
        antMatcher("/swagger-ui.html"),
        antMatcher("/swagger-ui/**"),
        antMatcher("/v3/api-docs/**"),
        antMatcher(AUTH_V1 + "/**"),
        antMatcher("/")
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(ALLOWED_PATHS)
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, TRANSACTIONS_V1)
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, TRANSACTIONS_V1)
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, TRANSACTIONS_GROUPED_V1)
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, TRANSACTION_PROCESS_FILE_V1)
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, TRANSACTION_STORE_NAME_V1)
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, TRANSACTION_STORE_BALANCE_V1)
                        .hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, TRANSACTION_CPF_V1)
                        .hasRole(ADMIN_ROLE)
                        .anyRequest()
                        .permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(new JwtConverter())))
                .cors(withDefaults())
                .build();
    }
}
