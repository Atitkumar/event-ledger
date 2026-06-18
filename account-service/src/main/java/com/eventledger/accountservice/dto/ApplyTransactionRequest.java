package com.eventledger.accountservice.dto;

import com.eventledger.accountservice.entity.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ApplyTransactionRequest {

    private String eventId;

    private TransactionType type;

    private BigDecimal amount;

    private String currency;

    private Instant eventTimestamp;
}