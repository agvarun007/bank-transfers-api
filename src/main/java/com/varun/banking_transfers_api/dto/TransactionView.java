package com.varun.banking_transfers_api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionView(
        UUID id, UUID from, UUID to, BigDecimal amount, Instant timestamp, String status
) {}