package com.varun.banking_transfers_api.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.varun.banking_transfers_api.dto.TransactionView;
import com.varun.banking_transfers_api.dto.TransferRequest;
import com.varun.banking_transfers_api.dto.TransferResponse;
import com.varun.banking_transfers_api.model.Transaction;
import com.varun.banking_transfers_api.service.TransferService;

import jakarta.validation.Valid;

@RestController
public class TransferController {
    private final TransferService transfers;

    public TransferController(TransferService transfers) { this.transfers = transfers; }

    @PostMapping("/transfers")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest req) {
        Transaction t = transfers.transfer(req.fromAccountId(), req.toAccountId(), req.amount(), req.idempotencyKey());
        return new TransferResponse(t.getId(), t.getStatus().name(), t.getTimestamp());
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<TransactionView> history(@PathVariable UUID id) {
        return transfers.history(id).stream()
                .map(t -> new TransactionView(t.getId(), t.getFrom(), t.getTo(), t.getAmount(), t.getTimestamp(), t.getStatus().name()))
                .toList();
    }
}