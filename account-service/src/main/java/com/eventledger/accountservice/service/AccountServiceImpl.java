package com.eventledger.accountservice.service;

import com.eventledger.accountservice.dto.ApplyTransactionRequest;
import com.eventledger.accountservice.dto.BalanceResponse;
import com.eventledger.accountservice.entity.Account;
import com.eventledger.accountservice.entity.AccountTransaction;
import com.eventledger.accountservice.entity.TransactionType;
import com.eventledger.accountservice.exception.AccountNotFoundException;
import com.eventledger.accountservice.exception.DuplicateTransactionException;
import com.eventledger.accountservice.repository.AccountRepository;
import com.eventledger.accountservice.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public BalanceResponse applyTransaction(
            String accountId,
            ApplyTransactionRequest request
    ) {

        transactionRepository.findByEventId(request.getEventId())
                .ifPresent(existing -> {
                    throw new DuplicateTransactionException(
                            request.getEventId()
                    );
                });

        Account account = accountRepository
                .findById(accountId)
                .orElseGet(() -> {

                    Account newAccount = new Account();
                    newAccount.setAccountId(accountId);
                    newAccount.setBalance(BigDecimal.ZERO);

                    return newAccount;
                });

        BigDecimal updatedBalance;

        if (request.getType() == TransactionType.CREDIT) {

            updatedBalance = account.getBalance()
                    .add(request.getAmount());

        } else {

            updatedBalance = account.getBalance()
                    .subtract(request.getAmount());
        }

        account.setBalance(updatedBalance);

        accountRepository.save(account);

        AccountTransaction transaction =
                new AccountTransaction();

        transaction.setEventId(request.getEventId());
        transaction.setAccountId(accountId);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setEventTimestamp(
                request.getEventTimestamp()
        );

        transactionRepository.save(transaction);

        return new BalanceResponse(
                accountId,
                account.getBalance()
        );
    }

    @Override
    public BalanceResponse getBalance(
            String accountId
    ) {

        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(
                        () -> new AccountNotFoundException(accountId)
                );

        return new BalanceResponse(
                accountId,
                account.getBalance()
        );
    }
}