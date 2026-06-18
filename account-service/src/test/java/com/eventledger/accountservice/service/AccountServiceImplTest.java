package com.eventledger.accountservice.service;

import com.eventledger.accountservice.dto.ApplyTransactionRequest;
import com.eventledger.accountservice.dto.BalanceResponse;
import com.eventledger.accountservice.entity.Account;
import com.eventledger.accountservice.entity.AccountTransaction;
import com.eventledger.accountservice.entity.TransactionType;
import com.eventledger.accountservice.exception.DuplicateTransactionException;
import com.eventledger.accountservice.repository.AccountRepository;
import com.eventledger.accountservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private AccountServiceImpl service;



    @Test
    void shouldApplyCreditTransaction() {

        Account account = new Account();
        account.setAccountId("acct-001");
        account.setBalance(BigDecimal.ZERO);

        when(accountRepository.findById("acct-001"))
                .thenReturn(Optional.of(account));

        ApplyTransactionRequest request =
                new ApplyTransactionRequest();

        request.setEventId("evt-001");
        request.setType(TransactionType.CREDIT);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setEventTimestamp(Instant.now());

        BalanceResponse response =
                service.applyTransaction("acct-001", request);

        assertEquals(
                new BigDecimal("100.00"),
                response.getBalance()
        );
    }
    @Test
    void shouldApplyDebitTransaction() {

        Account account = new Account();
        account.setAccountId("acct-001");
        account.setBalance(new BigDecimal("200.00"));

        when(accountRepository.findById("acct-001"))
                .thenReturn(Optional.of(account));

        ApplyTransactionRequest request =
                new ApplyTransactionRequest();

        request.setEventId("evt-002");
        request.setType(TransactionType.DEBIT);
        request.setAmount(new BigDecimal("50.00"));
        request.setCurrency("USD");
        request.setEventTimestamp(Instant.now());

        BalanceResponse response =
                service.applyTransaction("acct-001", request);

        assertEquals(
                new BigDecimal("150.00"),
                response.getBalance()
        );
    }
    @Test
    void shouldRejectDuplicateEvent() {

        AccountTransaction existing =
                new AccountTransaction();

        existing.setEventId("evt-001");

        when(transactionRepository.findByEventId("evt-001"))
                .thenReturn(Optional.of(existing));

        ApplyTransactionRequest request =
                new ApplyTransactionRequest();

        request.setEventId("evt-001");

        assertThrows(
                DuplicateTransactionException.class,
                () -> service.applyTransaction(
                        "acct-001",
                        request
                )
        );
    }
}
