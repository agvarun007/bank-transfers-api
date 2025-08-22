package com.varun.banking_transfers_api.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Transaction {
    public enum Status { SUCCESS, FAILED }

    private final UUID id;
    private final UUID from;
    private final UUID to;
    private final BigDecimal amount;
    private final Instant timestamp;
    private final Status status;

    public Transaction(UUID id, UUID from, UUID to, BigDecimal amount, Instant timestamp, Status status) {
        this.id = id; this.from = from; this.to = to; this.amount = amount; this.timestamp = timestamp; this.status = status;
    }
    public UUID getId() { return id; }
    public UUID getFrom() { return from; }
    public UUID getTo() { return to; }
    public BigDecimal getAmount() { return amount; }
    public Instant getTimestamp() { return timestamp; }
    public Status getStatus() { return status; }
}