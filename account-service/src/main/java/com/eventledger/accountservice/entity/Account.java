package com.eventledger.accountservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

}