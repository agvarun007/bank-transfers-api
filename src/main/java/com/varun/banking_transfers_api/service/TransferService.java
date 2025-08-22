package com.varun.banking_transfers_api.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import com.varun.banking_transfers_api.model.Account;
import com.varun.banking_transfers_api.model.Transaction;
import com.varun.banking_transfers_api.repo.AccountRepository;
import com.varun.banking_transfers_api.repo.TransactionRepository;

@Service
public class TransferService {
    private final AccountRepository accounts;
    private final TransactionRepository txRepo;
    private final Map<UUID, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final Map<String, UUID> idempotency = new ConcurrentHashMap<>();

    public TransferService(AccountRepository accounts, TransactionRepository txRepo) {
        this.accounts = accounts; this.txRepo = txRepo;
    }

    public Transaction transfer(UUID fromId, UUID toId, BigDecimal amount, String idemKey) {
        if (fromId.equals(toId)) throw new IllegalArgumentException("same account");
        if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("amount must be > 0");

        if (idemKey != null && !idemKey.isBlank()) {
            UUID existing = idempotency.get(idemKey);
            if (existing != null) return txRepo.findById(existing);
        }


        Account from = accounts.find(fromId).orElseThrow(() -> new NoSuchElementException("from not found"));
        Account to   = accounts.find(toId).orElseThrow(() -> new NoSuchElementException("to not found"));

        ReentrantLock l1 = locks.computeIfAbsent(min(fromId, toId), k -> new ReentrantLock());
        ReentrantLock l2 = locks.computeIfAbsent(max(fromId, toId), k -> new ReentrantLock());

        l1.lock();
        try {
            l2.lock();
            try {
                if (from.getBalance().compareTo(amount) < 0) throw new IllegalStateException("insufficient funds");
                from.setBalance(from.getBalance().subtract(amount));
                to.setBalance(to.getBalance().add(amount));
                accounts.save(from);
                accounts.save(to);

                Transaction t = new Transaction(UUID.randomUUID(), fromId, toId, amount, Instant.now(), Transaction.Status.SUCCESS);
                txRepo.save(t);
                if (idemKey != null && !idemKey.isBlank()) idempotency.putIfAbsent(idemKey, t.getId());
                return t;
            } finally {
                l2.unlock();
            }
        } finally {
            l1.unlock();
        }
    }

    public List<Transaction> history(UUID accountId) {
        return txRepo.findByAccount(accountId);
    }

    private static UUID min(UUID a, UUID b) { return a.compareTo(b) <= 0 ? a : b; }
    private static UUID max(UUID a, UUID b) { return a.compareTo(b) >= 0 ? a : b; }
}