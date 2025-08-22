package com.varun.banking_transfers_api;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.varun.banking_transfers_api.model.Account;
import com.varun.banking_transfers_api.model.Transaction;
import com.varun.banking_transfers_api.repo.InMemoryAccountRepository;
import com.varun.banking_transfers_api.repo.InMemoryTransactionRepository;
import com.varun.banking_transfers_api.service.TransferService;

class TransferServiceTest {

    @Test
    void transfer_updatesBalances_and_savesTransaction() {
        var accounts = new InMemoryAccountRepository();
        var txRepo = new InMemoryTransactionRepository();
        var svc = new TransferService(accounts, txRepo);

        var a = new Account(UUID.randomUUID(), "Alice", new BigDecimal("100.00"));
        var b = new Account(UUID.randomUUID(), "Bob",   new BigDecimal("50.00"));
        accounts.save(a); accounts.save(b);

        Transaction t = svc.transfer(a.getId(), b.getId(), new BigDecimal("25.00"), null);

        assertNotNull(t.getId());
        assertEquals(new BigDecimal("75.00"), accounts.find(a.getId()).orElseThrow().getBalance());
        assertEquals(new BigDecimal("75.00"), accounts.find(b.getId()).orElseThrow().getBalance());
    }

    @Test
    void idempotency_returnsSameTransaction_onRepeatKey() {
        var accounts = new InMemoryAccountRepository();
        var txRepo = new InMemoryTransactionRepository();
        var svc = new TransferService(accounts, txRepo);

        var a = new Account(UUID.randomUUID(), "A", new BigDecimal("100.00"));
        var b = new Account(UUID.randomUUID(), "B", new BigDecimal("50.00"));
        accounts.save(a); accounts.save(b);

        String key = "demo-key-1";
        Transaction t1 = svc.transfer(a.getId(), b.getId(), new BigDecimal("25.00"), key);
        Transaction t2 = svc.transfer(a.getId(), b.getId(), new BigDecimal("25.00"), key);

        assertEquals(t1.getId(), t2.getId());
    }

    @Test
    void insufficientFunds_throwsIllegalState() {
        var accounts = new InMemoryAccountRepository();
        var txRepo = new InMemoryTransactionRepository();
        var svc = new TransferService(accounts, txRepo);

        var a = new Account(UUID.randomUUID(), "A", new BigDecimal("10.00"));
        var b = new Account(UUID.randomUUID(), "B", new BigDecimal("50.00"));
        accounts.save(a); accounts.save(b);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> svc.transfer(a.getId(), b.getId(), new BigDecimal("1000.00"), null));
        assertEquals("insufficient funds", exception.getMessage());
    }

    @Test
    void sameAccount_throwsIllegalArgument() {
        var accounts = new InMemoryAccountRepository();
        var txRepo = new InMemoryTransactionRepository();
        var svc = new TransferService(accounts, txRepo);

        var a = new Account(UUID.randomUUID(), "A", new BigDecimal("100.00"));
        accounts.save(a);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> svc.transfer(a.getId(), a.getId(), new BigDecimal("1.00"), null));
        assertEquals("same account", exception.getMessage());
    }
}
