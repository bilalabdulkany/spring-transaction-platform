package com.company.transactionplatform.infrastructure.messaging;

import com.company.transactionplatform.infrastructure.persistence.entity.OutboxEventEntity;
import com.company.transactionplatform.infrastructure.persistence.repository.OutboxEventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxPublisherJob {

    private final OutboxEventRepository repository;
    private final TransactionEventProducer producer;

    public OutboxPublisherJob(OutboxEventRepository repository, TransactionEventProducer producer) {
        this.repository = repository;
        this.producer = producer;
    }

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEventEntity> events = repository.findPendingForUpdate(100);

        for (OutboxEventEntity event : events) {
            try {
                producer.publish(event);
                event.markProcessed();
            } catch (Exception ex) {
                event.markFailedAttempt();
            }
        }
    }
}
