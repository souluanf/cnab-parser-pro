package dev.luanfernandes.service.impl;

import static dev.luanfernandes.domain.utils.CNABValidator.isLinhaValida;
import static dev.luanfernandes.domain.utils.CPFValidator.isValidCPF;
import static java.lang.System.currentTimeMillis;
import static java.math.BigDecimal.ZERO;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.List.of;
import static java.util.stream.Collectors.groupingBy;

import dev.luanfernandes.domain.entity.Transaction;
import dev.luanfernandes.domain.enums.TransactionType;
import dev.luanfernandes.domain.exception.FileProcessingException;
import dev.luanfernandes.domain.exception.NotFoundException;
import dev.luanfernandes.domain.mapper.TransactionMapper;
import dev.luanfernandes.domain.response.BalanceResponse;
import dev.luanfernandes.domain.response.ErrorProcessing;
import dev.luanfernandes.domain.response.ProcessFileResponse;
import dev.luanfernandes.domain.response.StoreResponse;
import dev.luanfernandes.domain.response.TransactionResponse;
import dev.luanfernandes.repository.TransactionRepository;
import dev.luanfernandes.service.TransactionService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public ProcessFileResponse processFile(MultipartFile file) {

        long inicio = currentTimeMillis();
        List<String> lines;
        List<ErrorProcessing> erros = new ArrayList<>();
        int successfulTransactions = 0;
        int currentLine = 0;
        try {
            lines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines()
                    .toList();
        } catch (IOException e) {
            throw new FileProcessingException("Erro ao ler o arquivo", of(new ErrorProcessing(0, "", e.getMessage())));
        }
        for (String linha : lines) {
            currentLine++;
            try {
                boolean linhaValida = isLinhaValida(linha);
                if (!linhaValida) {
                    erros.add(new ErrorProcessing(currentLine, linha.trim(), "Linha inválida"));
                    continue;
                }

                int tipoId = Integer.parseInt(linha.substring(0, 1));
                TransactionType tipo = TransactionType.fromCode(tipoId);

                BigDecimal valor = new BigDecimal(linha.substring(9, 19))
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(tipo.getSignal().equals("+") ? "1" : "-1"));

                Transaction transaction = Transaction.builder()
                        .type(tipo)
                        .date(LocalDate.parse(linha.substring(1, 9), ofPattern("yyyyMMdd")))
                        .value(valor)
                        .cpf(linha.substring(19, 30))
                        .card(linha.substring(30, 42))
                        .hour(LocalTime.parse(linha.substring(42, 48), ofPattern("HHmmss")))
                        .storeOwner(linha.substring(48, 62).trim())
                        .storeName(linha.substring(62).trim())
                        .build();

                transactionRepository.save(transaction);
                successfulTransactions++;
            } catch (DataIntegrityViolationException e) {
                erros.add(new ErrorProcessing(currentLine, linha.trim(), "Transação duplicada"));
            } catch (Exception e) {
                erros.add(new ErrorProcessing(currentLine, linha.trim(), "Erro ao processar linha"));
            }
        }
        if (!erros.isEmpty()) {
            throw new FileProcessingException("Erro ao processar o arquivo CNAB", erros);
        }
        long tempoProcessamento = currentTimeMillis() - inicio;
        return new ProcessFileResponse(lines.size(), successfulTransactions, tempoProcessamento + "ms");
    }

    @Override
    public List<StoreResponse> getTransactionsGrouped() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .collect(groupingBy(t -> Map.of(
                        "name", t.getStoreName(),
                        "owner", t.getStoreOwner())))
                .entrySet()
                .stream()
                .map(entry -> {
                    String nomeLoja = entry.getKey().get("name");
                    String donoLoja = entry.getKey().get("owner");
                    BigDecimal totalBalance =
                            entry.getValue().stream().map(Transaction::getValue).reduce(ZERO, BigDecimal::add);
                    List<TransactionResponse> transactionsResponse = entry.getValue().stream()
                            .map(transactionMapper::map)
                            .toList();
                    return new StoreResponse(nomeLoja, donoLoja, totalBalance, transactionsResponse);
                })
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByStore(String nomeLoja) {
        List<TransactionResponse> transactions = transactionRepository.findByStoreName(nomeLoja).stream()
                .map(transactionMapper::map)
                .toList();
        if (transactions.isEmpty()) {
            throw new NotFoundException("Loja não encontrada: " + nomeLoja);
        }
        return transactions;
    }

    public List<BalanceResponse> getStoresBalances() {
        return transactionRepository.findAll().stream()
                .collect(groupingBy(t -> new BalanceResponse(t.getStoreName(), t.getStoreOwner(), ZERO)))
                .entrySet()
                .stream()
                .map(entry -> new BalanceResponse(
                        entry.getKey().storeName(),
                        entry.getKey().storeOwner(),
                        entry.getValue().stream().map(Transaction::getValue).reduce(ZERO, BigDecimal::add)))
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactionsByCPF(String cpf) {
        if (!isValidCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido: " + cpf);
        }
        return transactionRepository.findByCpf(cpf).stream()
                .map(transactionMapper::map)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::map)
                .toList();
    }
}
