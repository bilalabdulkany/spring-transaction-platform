package com.company.transactionplatform.api.response;

import com.company.transactionplatform.infrastructure.persistence.entity.TransactionEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(
        UUID transactionId,
        String status,
        BigDecimal amount,
        String currency,
        Instant createdAt
) {
    public static TransactionResponse from(TransactionEntity entity) {
        return new TransactionResponse(
                entity.getId(),
                entity.getStatus(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getCreatedAt()
        );
    }
}
