package com.eventledger.accountservice.service;

import com.eventledger.accountservice.dto.ApplyTransactionRequest;
import com.eventledger.accountservice.dto.BalanceResponse;

public interface AccountService {

    BalanceResponse applyTransaction(
            String accountId,
            ApplyTransactionRequest request
    );

    BalanceResponse getBalance(
            String accountId
    );
}