package com.varun.banking_transfers_api.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.varun.banking_transfers_api.model.Account;
import com.varun.banking_transfers_api.repo.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository repo;

    public AccountService(AccountRepository repo) { this.repo = repo; }

    public Account create(String name, BigDecimal initial) {
        UUID id = UUID.randomUUID();
        Account a = new Account(id, name, initial == null ? BigDecimal.ZERO : initial);
        return repo.save(a);
    }

    public Optional<Account> get(UUID id) { return repo.find(id); }
}