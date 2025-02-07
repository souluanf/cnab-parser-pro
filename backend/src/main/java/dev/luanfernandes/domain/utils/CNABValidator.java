package dev.luanfernandes.domain.utils;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CNABValidator {
    private static final String CNAB_REGEX = "^[1-9]\\d{8}\\d{10}\\d{11}[0-9*]{12}\\d{6}.*$";
    private static final Pattern PATTERN = compile(CNAB_REGEX);

    public static boolean isLinhaValida(String linha) {
        return PATTERN.matcher(linha).matches();
    }
}
