package com.eventledger.gateway.dto;

import com.eventledger.gateway.entity.EventStatus;
import com.eventledger.gateway.entity.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
public class EventResponse {

    private String eventId;

    private String accountId;

    private EventType type;

    private BigDecimal amount;

    private String currency;

    private Instant eventTimestamp;

    private EventStatus status;
}