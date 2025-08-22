package com.varun.banking_transfers_api.repo;

import java.util.List;
import java.util.UUID;

import com.varun.banking_transfers_api.model.Transaction;

public interface TransactionRepository {
    Transaction save(Transaction t);
    List<Transaction> findByAccount(UUID accountId);
    Transaction findById(UUID id);
}