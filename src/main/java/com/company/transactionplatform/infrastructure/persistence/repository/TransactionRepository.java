package com.company.transactionplatform.infrastructure.persistence.repository;

import com.company.transactionplatform.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    boolean existsByIdempotencyKey(String idempotencyKey);
    Optional<TransactionEntity> findByIdempotencyKey(String idempotencyKey);
}
