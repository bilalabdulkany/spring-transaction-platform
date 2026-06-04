# Transaction Platform

Java 21 + Spring Boot transaction system template.

## Contains

- PostgreSQL as source of truth
- Redis cache-aside balance cache
- Pessimistic row locking for account updates
- Optimistic version column using JPA `@Version`
- Idempotency key on transaction request
- Outbox pattern for reliable event publishing
- Kafka producer and consumer
- Consumer idempotency using `processed_events`
- Flyway migrations

## Main Patterns
- Clean Architecture
- Outbox Pattern
- Inbox Pattern
- Idempotency Key
- Pessimistic Locking
- Optimistic Versioning
- Cache Aside
- Eventual Consistency
- Retry with Dead Letter Queue
- Consumer Idempotency

## Important Part:

Never publish the event directly before the DB transaction commits.

Instead:

- DB transaction:
   - update business tables
   - insert outbox event
commit

- Background publisher:
  - read outbox event
  - publish to Kafka
  - mark outbox event processed
    
## Run infrastructure

```bash
docker compose up -d
```

## Run application

```bash
mvn spring-boot:run
```

## Test API

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "idempotencyKey": "REQ-100001",
    "fromAccountNumber": "ACC-1001",
    "toAccountNumber": "ACC-2001",
    "amount": 250.00,
    "currency": "AED"
  }'
```
