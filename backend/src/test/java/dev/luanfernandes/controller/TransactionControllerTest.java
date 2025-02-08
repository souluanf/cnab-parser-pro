package dev.luanfernandes.controller;

import static dev.luanfernandes.domain.constants.PathConstants.TRANSACTIONS_V1;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import dev.luanfernandes.config.security.WebSecurityConfig;
import dev.luanfernandes.config.web.ExceptionHandlerAdvice;
import dev.luanfernandes.controller.impl.TransactionControllerImpl;
import dev.luanfernandes.domain.response.BalanceResponse;
import dev.luanfernandes.domain.response.ProcessFileResponse;
import dev.luanfernandes.domain.response.StoreResponse;
import dev.luanfernandes.domain.response.TransactionResponse;
import dev.luanfernandes.service.TransactionService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@WebMvcTest
@ContextConfiguration(
        classes = {
            TransactionControllerImpl.class,
            ExceptionHandlerAdvice.class,
            WebSecurityConfig.class,
        })
@WithMockUser(roles = {"ADMIN"})
@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @InjectMocks
    private TransactionControllerImpl transactionController;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void deveRetornarTodasAsTransacoes() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .type("DEPOSITO")
                .description("Depósito realizado")
                .date("2023-12-01")
                .value(new BigDecimal("200.00"))
                .cpf("12345678901")
                .card("1234****5678")
                .hour("10:00")
                .storeOwner("Marcos Pereira")
                .storeName("Mercado da Avenida")
                .build();

        when(transactionService.getTransactions()).thenReturn(List.of(response));

        mockMvc.perform(get(TRANSACTIONS_V1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cpf").value("12345678901"))
                .andExpect(jsonPath("$[0].value").value("200.0"));
    }

    @Test
    void deveProcessarArquivoComSucesso() throws Exception {
        ProcessFileResponse response = new ProcessFileResponse(5, 5, "100ms");
        when(transactionService.processFile(any(MultipartFile.class))).thenReturn(response);

        mockMvc.perform(multipart(TRANSACTIONS_V1 + "/process-file").file("file", new byte[10]))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalLinesFile").value(5))
                .andExpect(jsonPath("$.totalProcessedLines").value(5))
                .andExpect(jsonPath("$.timeProcessing").value("100ms"));
    }

    @Test
    void deveRetornarTransacoesAgrupadas() throws Exception {
        StoreResponse storeResponse = StoreResponse.builder()
                .storeName("Mercado da Avenida")
                .storeOwner("Marcos Pereira")
                .totalBalance(new BigDecimal("200.00"))
                .transactions(List.of())
                .build();
        when(transactionService.getTransactionsGrouped()).thenReturn(List.of(storeResponse));

        mockMvc.perform(get(TRANSACTIONS_V1 + "/grouped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName").value("Mercado da Avenida"))
                .andExpect(jsonPath("$[0].storeOwner").value("Marcos Pereira"))
                .andExpect(jsonPath("$[0].totalBalance").value(200.0));
    }

    @Test
    void deveRetornarSaldoDasLojas() throws Exception {
        BalanceResponse balanceResponse =
                new BalanceResponse("Mercado da Avenida", "Marcos Pereira", new BigDecimal("300.00"));
        when(transactionService.getStoresBalances()).thenReturn(List.of(balanceResponse));

        mockMvc.perform(get(TRANSACTIONS_V1 + "/store/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].storeName").value("Mercado da Avenida"))
                .andExpect(jsonPath("$[0].storeOwner").value("Marcos Pereira"))
                .andExpect(jsonPath("$[0].totalBalance").value(300.0));
    }

    @Test
    void deveRetornarTransacoesPorCPF() throws Exception {
        TransactionResponse transactionResponse = TransactionResponse.builder()
                .id(1L)
                .type("DEPOSITO")
                .description("Depósito realizado")
                .date("2023-12-01")
                .value(new BigDecimal("100.00"))
                .cpf("12345678901")
                .card("1234****5678")
                .hour("10:30")
                .storeOwner("Marcos Pereira")
                .storeName("Mercado da Avenida")
                .build();
        when(transactionService.getTransactionsByCPF("12345678901")).thenReturn(List.of(transactionResponse));

        mockMvc.perform(get(TRANSACTIONS_V1 + "/cpf/12345678901"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cpf").value("12345678901"))
                .andExpect(jsonPath("$[0].value").value(100.0));
    }

    @Test
    void deveRetornarTransacoesPorLoja() throws Exception {
        List<TransactionResponse> storeResponse = List.of(TransactionResponse.builder()
                .id(1L)
                .type("DEPOSITO")
                .description("Depósito realizado")
                .date("2023-12-01")
                .value(new BigDecimal("150.00"))
                .cpf("12345678901")
                .card("1234****5678")
                .hour("10:30")
                .storeOwner("Marcos Pereira")
                .storeName("Mercado da Avenida")
                .build());

        when(transactionService.getTransactionsByStore("Mercado da Avenida")).thenReturn(storeResponse);

        mockMvc.perform(get(TRANSACTIONS_V1 + "/store/Mercado%20da%20Avenida")).andExpect(status().isOk());
    }
}
