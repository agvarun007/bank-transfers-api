package com.varun.banking_transfers_api.dto;

import java.time.Instant;
import java.util.UUID;

public record TransferResponse(UUID transactionId, String status, Instant timestamp) {}