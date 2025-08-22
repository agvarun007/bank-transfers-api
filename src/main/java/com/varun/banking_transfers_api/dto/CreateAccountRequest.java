package com.varun.banking_transfers_api.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
        @NotBlank String name,
        @DecimalMin("0.00") BigDecimal initialBalance
) {}
