package dev.luanfernandes.service;

import dev.luanfernandes.domain.response.BalanceResponse;
import dev.luanfernandes.domain.response.ProcessFileResponse;
import dev.luanfernandes.domain.response.StoreResponse;
import dev.luanfernandes.domain.response.TransactionResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TransactionService {
    ProcessFileResponse processFile(MultipartFile file);

    List<StoreResponse> getTransactionsGrouped();

    List<TransactionResponse> getTransactionsByStore(String nomeLoja);

    List<BalanceResponse> getStoresBalances();

    List<TransactionResponse> getTransactionsByCPF(String cpf);

    List<TransactionResponse> getTransactions();
}
