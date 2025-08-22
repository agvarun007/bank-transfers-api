package com.varun.banking_transfers_api.repo;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.varun.banking_transfers_api.model.Transaction;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<UUID, Transaction> tx = new ConcurrentHashMap<>();

    @Override public Transaction save(Transaction t) { tx.put(t.getId(), t); return t; }

    @Override public List<Transaction> findByAccount(UUID accountId) {
        List<Transaction> out = new ArrayList<>();
        for (Transaction t : tx.values()) {
            if (accountId.equals(t.getFrom()) || accountId.equals(t.getTo())) out.add(t);
        }
        out.sort(Comparator.comparing(Transaction::getTimestamp).reversed());
        return out;
    }
    @Override public Transaction findById(UUID id) { return tx.get(id); }

}
