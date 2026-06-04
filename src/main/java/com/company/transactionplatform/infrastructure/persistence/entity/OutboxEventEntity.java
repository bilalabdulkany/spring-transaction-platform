package com.company.transactionplatform.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, length = 100)
    private String aggregateType;

    @Column(nullable = false, length = 100)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false, length = 30)
    private String status;

    private int retryCount;
    private Instant createdAt;
    private Instant processedAt;

    protected OutboxEventEntity() {}

    public static OutboxEventEntity create(UUID aggregateId, String aggregateType, String eventType, Map<String, Object> payload) {
        OutboxEventEntity entity = new OutboxEventEntity();
        entity.id = UUID.randomUUID();
        entity.aggregateId = aggregateId;
        entity.aggregateType = aggregateType;
        entity.eventType = eventType;
        entity.payload = payload;
        entity.status = "PENDING";
        entity.retryCount = 0;
        entity.createdAt = Instant.now();
        return entity;
    }

    public void markProcessed() {
        this.status = "PROCESSED";
        this.processedAt = Instant.now();
    }

    public void markFailedAttempt() {
        this.retryCount++;
        if (this.retryCount >= 5) {
            this.status = "FAILED";
        }
    }

    public UUID getId() { return id; }
    public UUID getAggregateId() { return aggregateId; }
    public String getAggregateType() { return aggregateType; }
    public String getEventType() { return eventType; }
    public Map<String, Object> getPayload() { return payload; }
    public String getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
}
