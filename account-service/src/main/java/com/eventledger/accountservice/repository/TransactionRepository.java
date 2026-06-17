package com.eventledger.accountservice.repository;

import com.eventledger.accountservice.entity.AccountTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<AccountTransaction, Long> {

    Optional<AccountTransaction> findByEventId(String eventId);
}