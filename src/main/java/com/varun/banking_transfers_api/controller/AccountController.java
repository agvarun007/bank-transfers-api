package com.varun.banking_transfers_api.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.varun.banking_transfers_api.dto.CreateAccountRequest;
import com.varun.banking_transfers_api.dto.CreateAccountResponse;
import com.varun.banking_transfers_api.model.Account;
import com.varun.banking_transfers_api.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accounts;

    public AccountController(AccountService accounts) { this.accounts = accounts; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateAccountResponse create(@Valid @RequestBody CreateAccountRequest req) {
        Account a = accounts.create(req.name(), req.initialBalance());
        return new CreateAccountResponse(a.getId(), a.getBalance());
    }

    @GetMapping("/{id}")
    public CreateAccountResponse get(@PathVariable UUID id) {
        Account a = accounts.get(id).orElseThrow(() -> new IllegalArgumentException("account not found"));
        return new CreateAccountResponse(a.getId(), a.getBalance());
    }
}