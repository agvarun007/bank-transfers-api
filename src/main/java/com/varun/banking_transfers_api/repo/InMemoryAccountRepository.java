package com.varun.banking_transfers_api.repo;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.varun.banking_transfers_api.model.Account;

@Repository
public class InMemoryAccountRepository implements AccountRepository {
    private final Map<UUID, Account> store = new ConcurrentHashMap<>();

    @Override public Account save(Account a) { store.put(a.getId(), a); return a; }
    @Override public Optional<Account> find(UUID id) { return Optional.ofNullable(store.get(id)); }
}