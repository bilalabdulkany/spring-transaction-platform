package com.company.transactionplatform.infrastructure.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
public class BalanceCacheService {
    private static final String KEY_PREFIX = "account:balance:";

    private final RedisTemplate<String, BigDecimal> redisTemplate;

    public BalanceCacheService(RedisTemplate<String, BigDecimal> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<BigDecimal> getBalance(String accountNumber) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(KEY_PREFIX + accountNumber));
    }

    public void setBalance(String accountNumber, BigDecimal balance) {
        redisTemplate.opsForValue().set(KEY_PREFIX + accountNumber, balance, Duration.ofMinutes(5));
    }

    public void evictBalance(String accountNumber) {
        redisTemplate.delete(KEY_PREFIX + accountNumber);
    }
}
