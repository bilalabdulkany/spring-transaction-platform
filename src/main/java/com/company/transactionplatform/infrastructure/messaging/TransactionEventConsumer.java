package com.company.transactionplatform.infrastructure.messaging;

import com.company.transactionplatform.infrastructure.persistence.entity.ProcessedEventEntity;
import com.company.transactionplatform.infrastructure.persistence.repository.ProcessedEventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class TransactionEventConsumer {

    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public TransactionEventConsumer(ProcessedEventRepository processedEventRepository, ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transaction-events", groupId = "transaction-audit-service")
    @Transactional
    public void consume(String payload) throws Exception {
        JsonNode json = objectMapper.readTree(payload);
        UUID eventId = UUID.fromString(json.get("eventId").asText());

        if (processedEventRepository.existsByEventId(eventId)) {
            return;
        }

        // Business processing goes here:
        // - audit log
        // - notification
        // - reporting projection
        // - reconciliation projection

        processedEventRepository.save(new ProcessedEventEntity(eventId));
    }
}
