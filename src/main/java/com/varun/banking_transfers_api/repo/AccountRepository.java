package com.varun.banking_transfers_api.repo;

import java.util.Optional;
import java.util.UUID;

import com.varun.banking_transfers_api.model.Account;

public interface AccountRepository {
    Account save(Account a);
    Optional<Account> find(UUID id);
}