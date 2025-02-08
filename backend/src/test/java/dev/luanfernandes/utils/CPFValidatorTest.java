package dev.luanfernandes.utils;

import static org.junit.jupiter.api.Assertions.*;

import dev.luanfernandes.domain.utils.CPFValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CPFValidatorTest {

    @Test
    @DisplayName("Deve validar CPF válido sem pontuação")
    void deveValidarCPFValidoSemPontuacao() {
        assertTrue(CPFValidator.isValidCPF("12345678909"));
    }

    @Test
    @DisplayName("Deve validar CPF válido com pontuação")
    void deveValidarCPFValidoComPontuacao() {
        assertTrue(CPFValidator.isValidCPF("123.456.789-09"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com formato inválido")
    void deveRejeitarCPFComFormatoInvalido() {
        assertFalse(CPFValidator.isValidCPF("123.456.78-909")); // Um dígito a menos
        assertFalse(CPFValidator.isValidCPF("12.3456.789-09")); // Formato errado
    }

    @Test
    @DisplayName("Deve rejeitar CPF contendo letras")
    void deveRejeitarCPFComLetras() {
        assertFalse(CPFValidator.isValidCPF("ABC.DEF.GHI-JK"));
        assertFalse(CPFValidator.isValidCPF("123.456.ABC-09"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF com menos ou mais de 11 dígitos")
    void deveRejeitarCPFComQuantidadeIncorretaDeDigitos() {
        assertFalse(CPFValidator.isValidCPF("123456789")); // 9 dígitos
        assertFalse(CPFValidator.isValidCPF("1234567890123")); // 13 dígitos
    }

    @Test
    @DisplayName("Deve rejeitar CPF com todos os dígitos iguais")
    void deveRejeitarCPFComTodosOsDigitosIguais() {
        assertFalse(CPFValidator.isValidCPF("00000000000"));
        assertFalse(CPFValidator.isValidCPF("11111111111"));
        assertFalse(CPFValidator.isValidCPF("22222222222"));
        assertFalse(CPFValidator.isValidCPF("33333333333"));
        assertFalse(CPFValidator.isValidCPF("44444444444"));
        assertFalse(CPFValidator.isValidCPF("55555555555"));
        assertFalse(CPFValidator.isValidCPF("66666666666"));
        assertFalse(CPFValidator.isValidCPF("77777777777"));
        assertFalse(CPFValidator.isValidCPF("88888888888"));
        assertFalse(CPFValidator.isValidCPF("99999999999"));
    }

    @Test
    @DisplayName("Deve rejeitar CPF nulo")
    void deveRejeitarCPFNull() {
        assertFalse(CPFValidator.isValidCPF(null));
    }
}
