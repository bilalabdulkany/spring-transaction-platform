package com.company.transactionplatform.application.command;

import java.math.BigDecimal;

public record CreateTransactionCommand(
        String idempotencyKey,
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String currency
) {}
