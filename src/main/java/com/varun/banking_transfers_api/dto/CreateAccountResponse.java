package com.varun.banking_transfers_api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAccountResponse(UUID accountId, BigDecimal balance) {}