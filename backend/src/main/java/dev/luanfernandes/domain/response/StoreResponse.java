package dev.luanfernandes.domain.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@Builder
public record StoreResponse(
        String storeName, String storeOwner, BigDecimal totalBalance, List<TransactionResponse> transactions) {}
