package com.taller.banco.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction {
    public enum Type { DEPOSIT, WITHDRAW, TRANSFER }

    private final String id = UUID.randomUUID().toString();
    private final Type type;
    private final String sourceAccountId;
    private final String targetAccountId; // null si no aplica
    private final BigDecimal amount;
    private final Instant timestamp = Instant.now();

    public Transaction(Type type, String sourceAccountId, String targetAccountId, BigDecimal amount) {
        this.type = type;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
    }

    public String getId() { return id; }
    public Type getType() { return type; }
    public String getSourceAccountId() { return sourceAccountId; }
    public String getTargetAccountId() { return targetAccountId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getTimestamp() { return timestamp; }
}