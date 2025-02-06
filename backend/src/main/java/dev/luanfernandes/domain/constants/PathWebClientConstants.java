package dev.luanfernandes.domain.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathWebClientConstants {
    private static final String BASE_REALMS = "/realms/{realm}";
    public static final String TOKEN = BASE_REALMS + "/protocol/openid-connect/token";
    public static final String LOGOUT = BASE_REALMS + "/protocol/openid-connect/logout";
}
