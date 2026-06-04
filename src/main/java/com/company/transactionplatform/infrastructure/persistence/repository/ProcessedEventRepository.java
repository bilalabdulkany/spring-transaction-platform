package com.company.transactionplatform.infrastructure.persistence.repository;

import com.company.transactionplatform.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
    boolean existsByEventId(UUID eventId);
}
