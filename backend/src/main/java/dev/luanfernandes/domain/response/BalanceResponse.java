package dev.luanfernandes.domain.response;

import java.math.BigDecimal;

public record BalanceResponse(String storeName, String storeOwner, BigDecimal totalBalance) {}
