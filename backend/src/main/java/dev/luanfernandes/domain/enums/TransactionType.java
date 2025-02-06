package dev.luanfernandes.domain.enums;

import static dev.luanfernandes.domain.constants.SystemConstants.ENTRADA;
import static dev.luanfernandes.domain.constants.SystemConstants.SAIDA;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    DEBIT(1, "Débito", ENTRADA, "+"),
    BANK_SLIP(2, "Boleto", SAIDA, "-"),
    FINANCING(3, "Financiamento", SAIDA, "-"),
    CREDIT(4, "Crédito", ENTRADA, "+"),
    LOAN_RECEIPT(5, "Recebimento Empréstimo", ENTRADA, "+"),
    SALES(6, "Vendas", ENTRADA, "+"),
    TED_RECEIPT(7, "Recebimento TED", ENTRADA, "+"),
    DOC_RECEIPT(8, "Recebimento DOC", ENTRADA, "+"),
    RENT(9, "Aluguel", SAIDA, "-");

    private final int code;
    private final String description;
    private final String nature;
    private final String signal;

    public static TransactionType fromCode(int code) {
        return Arrays.stream(TransactionType.values())
                .filter(tipo -> tipo.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Código de transação desconhecido: " + code));
    }
}
