package com.eventledger.accountservice.controller;

import com.eventledger.accountservice.dto.ApplyTransactionRequest;
import com.eventledger.accountservice.dto.AccountDetailsResponse;
import com.eventledger.accountservice.dto.BalanceResponse;
import com.eventledger.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/{accountId}/transactions")
    public BalanceResponse applyTransaction(
            @PathVariable String accountId,
            @RequestBody ApplyTransactionRequest request
    ) {

        return accountService
                .applyTransaction(accountId, request);
    }

    @GetMapping("/{accountId}/balance")
    public BalanceResponse getBalance(
            @PathVariable String accountId
    ) {

        return accountService.getBalance(accountId);
    }

    @GetMapping("/{accountId}")
    public AccountDetailsResponse getAccount(
            @PathVariable String accountId
    ) {

        return accountService
                .getAccountDetails(accountId);
    }
}