package com.varun.banking_transfers_api.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Account {
    private final UUID id;
    private final String name;
    private BigDecimal balance;

    public Account(UUID id, String name, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal b) { this.balance = b; }
}