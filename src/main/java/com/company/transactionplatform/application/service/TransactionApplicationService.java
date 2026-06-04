package com.company.transactionplatform.application.service;

import com.company.transactionplatform.api.response.TransactionResponse;
import com.company.transactionplatform.application.command.CreateTransactionCommand;
import com.company.transactionplatform.infrastructure.cache.BalanceCacheService;
import com.company.transactionplatform.infrastructure.persistence.entity.AccountEntity;
import com.company.transactionplatform.infrastructure.persistence.entity.OutboxEventEntity;
import com.company.transactionplatform.infrastructure.persistence.entity.TransactionEntity;
import com.company.transactionplatform.infrastructure.persistence.repository.AccountRepository;
import com.company.transactionplatform.infrastructure.persistence.repository.OutboxEventRepository;
import com.company.transactionplatform.infrastructure.persistence.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class TransactionApplicationService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final BalanceCacheService balanceCacheService;

    public TransactionApplicationService(AccountRepository accountRepository,
                                         TransactionRepository transactionRepository,
                                         OutboxEventRepository outboxEventRepository,
                                         BalanceCacheService balanceCacheService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.balanceCacheService = balanceCacheService;
    }

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionCommand command) {
        return transactionRepository.findByIdempotencyKey(command.idempotencyKey())
                .map(TransactionResponse::from)
                .orElseGet(() -> createNewTransaction(command));
    }

    private TransactionResponse createNewTransaction(CreateTransactionCommand command) {
        AccountEntity from = accountRepository.findByAccountNumber(command.fromAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("From account not found"));

        AccountEntity to = accountRepository.findByAccountNumber(command.toAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("To account not found"));

        if (!from.getCurrency().equals(command.currency()) || !to.getCurrency().equals(command.currency())) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        List<AccountEntity> locked = lockAccountsInOrder(from.getId(), to.getId());

        AccountEntity lockedFrom = locked.stream()
                .filter(x -> x.getId().equals(from.getId()))
                .findFirst()
                .orElseThrow();

        AccountEntity lockedTo = locked.stream()
                .filter(x -> x.getId().equals(to.getId()))
                .findFirst()
                .orElseThrow();

        lockedFrom.debit(command.amount());
        lockedTo.credit(command.amount());

        TransactionEntity transaction = TransactionEntity.create(
                command.idempotencyKey(),
                lockedFrom.getId(),
                lockedTo.getId(),
                command.amount(),
                command.currency()
        );

        transactionRepository.save(transaction);

        Map<String, Object> eventPayload = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "transactionId", transaction.getId().toString(),
                "fromAccountId", lockedFrom.getId().toString(),
                "toAccountId", lockedTo.getId().toString(),
                "amount", command.amount(),
                "currency", command.currency(),
                "occurredAt", Instant.now().toString()
        );

        outboxEventRepository.save(OutboxEventEntity.create(
                transaction.getId(),
                "Transaction",
                "TransactionCreated",
                eventPayload
        ));

        balanceCacheService.evictBalance(lockedFrom.getAccountNumber());
        balanceCacheService.evictBalance(lockedTo.getAccountNumber());

        return TransactionResponse.from(transaction);
    }

    private List<AccountEntity> lockAccountsInOrder(UUID fromId, UUID toId) {
        List<UUID> orderedIds = Stream.of(fromId, toId).sorted().toList();

        AccountEntity first = accountRepository.findByIdForUpdate(orderedIds.get(0)).orElseThrow();
        AccountEntity second = accountRepository.findByIdForUpdate(orderedIds.get(1)).orElseThrow();

        if (first.getId().equals(fromId)) {
            return List.of(first, second);
        }
        return List.of(second, first);
    }
}
