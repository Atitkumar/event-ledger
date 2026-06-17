package com.eventledger.accountservice.repository;

import com.eventledger.accountservice.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository repository;

    @Test
    void shouldSaveAccount() {

        Account account = new Account();
        account.setAccountId("acct-001");
        account.setBalance(BigDecimal.ZERO);

        Account saved = repository.save(account);

        assertThat(saved.getAccountId())
                .isEqualTo("acct-001");
    }
}