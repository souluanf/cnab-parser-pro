package dev.luanfernandes.domain.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathConstants {
    private static final String API_V1 = "/v1";

    public static final String AUTH_V1 = API_V1 + "/auth";
    public static final String AUTH_TOKEN_V1 = AUTH_V1 + "/token";
    public static final String AUTH_REFRESH_TOKEN_V1 = AUTH_V1 + "/refresh-token";
    public static final String AUTH_LOGOUT_V1 = AUTH_V1 + "/logout";

    public static final String TRANSACTIONS_V1 = API_V1 + "/transactions";
    public static final String TRANSACTIONS_GROUPED_V1 = API_V1 + "/transactions/grouped";
    public static final String TRANSACTION_PROCESS_FILE_V1 = TRANSACTIONS_V1 + "/process-file";
    public static final String TRANSACTION_STORE_NAME_V1 = TRANSACTIONS_V1 + "/store/{name}";
    public static final String TRANSACTION_STORE_BALANCE_V1 = TRANSACTIONS_V1 + "/store/balance";
    public static final String TRANSACTION_CPF_V1 = TRANSACTIONS_V1 + "/cpf/{cpf}";
}
