package dev.luanfernandes.domain.utils;

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CPFValidator {
    private static final Pattern CPF_PATTERN = compile("(\\d{3}[.]?\\d{3}[.]?\\d{3}-\\d{2})|(\\d{11})");
    private static final Pattern[] INVALID_CPFS = {
        compile("^(?:(?!000\\.?000\\.?000-?00).)*$"),
        compile("^(?:(?!111\\.?111\\.?111-?11).)*$"),
        compile("^(?:(?!222\\.?222\\.?222-?22).)*$"),
        compile("^(?:(?!333\\.?333\\.?333-?33).)*$"),
        compile("^(?:(?!444\\.?444\\.?444-?44).)*$"),
        compile("^(?:(?!555\\.?555\\.?555-?55).)*$"),
        compile("^(?:(?!666\\.?666\\.?666-?66).)*$"),
        compile("^(?:(?!777\\.?777\\.?777-?77).)*$"),
        compile("^(?:(?!888\\.?888\\.?888-?88).)*$"),
        compile("^(?:(?!999\\.?999\\.?999-?99).)*$")
    };

    public static boolean isValidCPF(String cpf) {
        if (cpf == null || !CPF_PATTERN.matcher(cpf).matches()) {
            return false;
        }
        for (Pattern pattern : INVALID_CPFS) {
            if (!pattern.matcher(cpf).matches()) {
                return false;
            }
        }
        return true;
    }
}
