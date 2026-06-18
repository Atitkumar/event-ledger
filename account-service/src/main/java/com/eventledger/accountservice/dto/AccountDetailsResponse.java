package com.eventledger.accountservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class AccountDetailsResponse {

    private String accountId;

    private BigDecimal balance;

    private List<TransactionSummary> transactions;
}