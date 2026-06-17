package com.eventledger.accountservice.repository;

import com.eventledger.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository
        extends JpaRepository<Account, String> {
}