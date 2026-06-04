package com.company.transactionplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private UUID fromAccountId;

    @Column(nullable = false)
    private UUID toAccountId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, length = 30)
    private String status;

    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;

    protected TransactionEntity() {}

    public static TransactionEntity create(String idempotencyKey, UUID fromAccountId, UUID toAccountId,
                                           BigDecimal amount, String currency) {
        TransactionEntity entity = new TransactionEntity();
        entity.id = UUID.randomUUID();
        entity.idempotencyKey = idempotencyKey;
        entity.fromAccountId = fromAccountId;
        entity.toAccountId = toAccountId;
        entity.amount = amount;
        entity.currency = currency;
        entity.status = "SUCCESS";
        entity.createdAt = Instant.now();
        entity.updatedAt = Instant.now();
        return entity;
    }

    public UUID getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public UUID getFromAccountId() { return fromAccountId; }
    public UUID getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
