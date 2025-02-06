package dev.luanfernandes.controller.impl;

import static org.springframework.http.ResponseEntity.ok;

import dev.luanfernandes.controller.TransactionController;
import dev.luanfernandes.domain.response.BalanceResponse;
import dev.luanfernandes.domain.response.ProcessFileResponse;
import dev.luanfernandes.domain.response.StoreResponse;
import dev.luanfernandes.domain.response.TransactionResponse;
import dev.luanfernandes.service.TransactionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class TransactionControllerImpl implements TransactionController {

    private final TransactionService transactionService;

    @Override
    public ResponseEntity<ProcessFileResponse> processFile(MultipartFile file) {
        return ok(transactionService.processFile(file));
    }

    @Override
    public ResponseEntity<List<StoreResponse>> getTransactionsGrouped() {
        return ok(transactionService.getTransactionsGrouped());
    }

    @Override
    public ResponseEntity<List<TransactionResponse>> getTransactions() {
        return ok(transactionService.getTransactions());
    }

    @Override
    public ResponseEntity<List<TransactionResponse>> getTransactionsByStore(String name) {
        return ok(transactionService.getTransactionsByStore(name));
    }

    @Override
    public ResponseEntity<List<BalanceResponse>> getStoresBalances() {
        return ok(transactionService.getStoresBalances());
    }

    @Override
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCPF(String cpf) {
        return ok(transactionService.getTransactionsByCPF(cpf));
    }
}
