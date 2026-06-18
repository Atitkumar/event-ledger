package com.eventledger.gateway.client.dto;

import com.eventledger.gateway.entity.EventType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class ApplyTransactionRequest {

    private String eventId;

    private EventType type;

    private BigDecimal amount;

    private String currency;

    private Instant eventTimestamp;
}