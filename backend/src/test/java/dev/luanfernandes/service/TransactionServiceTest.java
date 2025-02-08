package dev.luanfernandes.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import dev.luanfernandes.domain.entity.Transaction;
import dev.luanfernandes.domain.exception.FileProcessingException;
import dev.luanfernandes.domain.exception.NotFoundException;
import dev.luanfernandes.domain.mapper.TransactionMapper;
import dev.luanfernandes.domain.response.BalanceResponse;
import dev.luanfernandes.domain.response.ProcessFileResponse;
import dev.luanfernandes.domain.response.StoreResponse;
import dev.luanfernandes.domain.response.TransactionResponse;
import dev.luanfernandes.repository.TransactionRepository;
import dev.luanfernandes.service.impl.TransactionServiceImpl;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Spy
    private TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction1, transaction2, transaction3;

    private MultipartFile mockFile(String content) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "mockFile";
            }

            @Override
            public String getOriginalFilename() {
                return "testfile.txt";
            }

            @Override
            public String getContentType() {
                return "text/plain";
            }

            @Override
            public boolean isEmpty() {
                return content.isEmpty();
            }

            @Override
            public long getSize() {
                return content.length();
            }

            @Override
            public byte[] getBytes() {
                return content.getBytes();
            }

            @Override
            public ByteArrayInputStream getInputStream() {
                return new ByteArrayInputStream(content.getBytes());
            }

            @Override
            public void transferTo(java.io.File dest) {
                /* Não usado */
            }
        };
    }

    @BeforeEach
    void setUp() {
        reset(transactionRepository);
        transaction1 = Transaction.builder()
                .type(null) // Defina um TransactionType válido se necessário
                .date(LocalDate.now())
                .value(new BigDecimal("100.00"))
                .cpf("12345678901")
                .card("1234****5678")
                .hour(LocalTime.now())
                .storeOwner("Marcos Pereira")
                .storeName("Mercado da Avenida")
                .build();

        transaction2 = Transaction.builder()
                .type(null)
                .date(LocalDate.now())
                .value(new BigDecimal("-50.00")) // Transação negativa (exemplo: saque)
                .cpf("98765432100")
                .card("4321****8765")
                .hour(LocalTime.now())
                .storeOwner("José Costa")
                .storeName("Mercearia 3 Irmãos")
                .build();

        transaction3 = Transaction.builder()
                .type(null)
                .date(LocalDate.now())
                .value(new BigDecimal("200.00"))
                .cpf("56789012345")
                .card("5678****1234")
                .hour(LocalTime.now())
                .storeOwner("Maria Josefina")
                .storeName("Loja do Ó - Filial")
                .build();
    }

    @Test
    void deveProcessarArquivoComSucesso() {
        String cnabData =
                """
                5201903010000080200845152540733123****7687145607MARCOS PEREIRAMERCADO DA AVENIDA
                2201903010000010200232702980568473****1231231233JOSÉ COSTA    MERCEARIA 3 IRMÃOS
                3201903010000610200232702980566777****1313172712JOSÉ COSTA    MERCEARIA 3 IRMÃOS
                """;

        MultipartFile file = mockFile(cnabData);
        ProcessFileResponse response = transactionService.processFile(file);

        assertEquals(3, response.totalLinesFile());
        assertEquals(3, response.totalProcessedLines());
        assertNotNull(response.timeProcessing());
    }

    @Test
    void deveLancarErroAoProcessarArquivoComLinhasInvalidas() {
        String cnabData =
                """
                5201903010000080200845152540733INVALIDO0000000000INVALIDO     MERCADO ERRADO
                220190301INVALID0000232702980568473****1231231233JOSÉ COSTA    MERCEARIA 3 IRMÃOS
                """;

        MultipartFile file = mockFile(cnabData);

        FileProcessingException exception =
                assertThrows(FileProcessingException.class, () -> transactionService.processFile(file));

        assertTrue(exception.getMessage().contains("Erro ao processar o arquivo CNAB"));
        assertEquals(2, exception.getErrors().size());
    }

    @Test
    void deveLancarErroAoNaoConseguirLerArquivo() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("Falha na leitura"));

        FileProcessingException exception =
                assertThrows(FileProcessingException.class, () -> transactionService.processFile(file));

        assertEquals("Erro ao ler o arquivo", exception.getMessage());
    }

    @Test
    void deveRetornarTransacoesAgrupadasPorLoja() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2, transaction3));

        List<StoreResponse> resultado = transactionService.getTransactionsGrouped();

        assertEquals(3, resultado.size());
    }

    @Test
    void deveRetornarTransacoesPorLoja() {
        when(transactionRepository.findByStoreName("Mercado da Avenida")).thenReturn(List.of(transaction1));

        List<TransactionResponse> response = transactionService.getTransactionsByStore("Mercado da Avenida");

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void deveLancarExcecaoSeLojaNaoForEncontrada() {
        when(transactionRepository.findByStoreName("Loja Inexistente")).thenReturn(List.of());

        NotFoundException exception = assertThrows(
                NotFoundException.class, () -> transactionService.getTransactionsByStore("Loja Inexistente"));

        assertEquals("Loja não encontrada: Loja Inexistente", exception.getMessage());
    }

    @Test
    void deveRetornarSaldosDasLojas() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2, transaction3));

        List<BalanceResponse> resultado = transactionService.getStoresBalances();

        assertEquals(3, resultado.size());
        assertEquals("Mercado da Avenida", resultado.getFirst().storeName());
        assertEquals(new BigDecimal("100.00"), resultado.getFirst().totalBalance());
    }

    @Test
    void deveRetornarTransacoesPorCPF() {
        when(transactionRepository.findByCpf("12345678901")).thenReturn(List.of(transaction1));

        List<TransactionResponse> resultado = transactionService.getTransactionsByCPF("12345678901");

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("12345678901", resultado.getFirst().cpf());
    }

    @Test
    void deveLancarExcecaoSeCPFInvalido() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> transactionService.getTransactionsByCPF("00000000000"));

        assertEquals("CPF inválido: 00000000000", exception.getMessage());
    }

    @Test
    void deveRetornarTodasTransacoes() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction1, transaction2, transaction3));

        List<TransactionResponse> resultado = transactionService.getTransactions();

        assertEquals(3, resultado.size());
        assertEquals("12345678901", resultado.get(0).cpf());
        assertEquals(new BigDecimal("100.00"), resultado.get(0).value());
    }

    @Test
    void deveRegistrarErroParaTransacoesDuplicadas() {
        String cnabData =
                """
            5201903010000080200845152540733123****7687145607MARCOS PEREIRAMERCADO DA AVENIDA
            5201903010000080200845152540733123****7687145607MARCOS PEREIRAMERCADO DA AVENIDA
            """;

        MultipartFile file = mockFile(cnabData);

        doThrow(new DataIntegrityViolationException("Transação duplicada"))
                .when(transactionRepository)
                .save(any(Transaction.class));

        FileProcessingException exception =
                assertThrows(FileProcessingException.class, () -> transactionService.processFile(file));

        assertTrue(exception.getMessage().contains("Erro ao processar o arquivo CNAB"));
        assertEquals(2, exception.getErrors().size());
        assertEquals("Transação duplicada", exception.getErrors().get(0).reason());
    }

    @Test
    void deveRegistrarErroAoProcessarLinhaComExcecaoNaoTratada() {
        String cnabData =
                """
            5201903010000080200845152540733123****7687145607MARCOS PEREIRAMERCADO DA AVENIDA
            2201903010000010200232702980568473****1231231233JOSÉ COSTA    MERCEARIA 3 IRMÃOS
            """;
        MultipartFile file = mockFile(cnabData);
        doThrow(new RuntimeException("Erro inesperado"))
                .when(transactionRepository)
                .save(any(Transaction.class));
        FileProcessingException exception =
                assertThrows(FileProcessingException.class, () -> transactionService.processFile(file));
        assertTrue(exception.getMessage().contains("Erro ao processar o arquivo CNAB"));
        assertEquals(2, exception.getErrors().size());
        assertTrue(
                exception.getErrors().stream().anyMatch(error -> error.reason().equals("Erro ao processar linha")));
    }
}
