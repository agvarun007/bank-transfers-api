package com.varun.banking_transfers_api;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.varun.banking_transfers_api.model.Account;
import com.varun.banking_transfers_api.repo.InMemoryAccountRepository;
import com.varun.banking_transfers_api.service.AccountService;

class AccountServiceTest {

    @Test
    void create_setsInitialBalance_orZero() {
        var repo = new InMemoryAccountRepository();
        var svc = new AccountService(repo);

        Account a = svc.create("Alice", new BigDecimal("100.00"));
        assertNotNull(a.getId());
        assertEquals(new BigDecimal("100.00"), a.getBalance());

        Account b = svc.create("Bob", null);
        assertNotNull(b.getId());
        assertEquals(BigDecimal.ZERO, b.getBalance());
    }
}
