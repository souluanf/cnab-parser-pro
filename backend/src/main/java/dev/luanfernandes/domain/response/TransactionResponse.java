package dev.luanfernandes.domain.response;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record TransactionResponse(
        Long id,
        String type,
        String description,
        String date,
        BigDecimal value,
        String cpf,
        String card,
        String hour,
        String storeOwner,
        String storeName) {}
