package com.eventledger.gateway.service;

import com.eventledger.gateway.client.AccountServiceClient;
import com.eventledger.gateway.dto.EventRequest;
import com.eventledger.gateway.dto.EventResponse;
import com.eventledger.gateway.entity.Event;
import com.eventledger.gateway.entity.EventStatus;
import com.eventledger.gateway.entity.EventType;
import com.eventledger.gateway.exception.AccountServiceUnavailableException;
import com.eventledger.gateway.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private EventServiceImpl service;


    @Test
    void shouldCreateEvent() throws Exception {

        EventRequest request = new EventRequest();

        request.setEventId("evt-001");
        request.setAccountId("acct-123");
        request.setType(EventType.CREDIT);
        request.setAmount(new BigDecimal("100.00"));
        request.setCurrency("USD");
        request.setEventTimestamp(Instant.now());
        request.setMetadata(Map.of("source", "test"));

        when(repository.findByEventId("evt-001"))
                .thenReturn(Optional.empty());

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"source\":\"test\"}");

        Event savedEvent = new Event();

        savedEvent.setId(1L);
        savedEvent.setEventId("evt-001");
        savedEvent.setAccountId("acct-123");
        savedEvent.setType(EventType.CREDIT);
        savedEvent.setAmount(new BigDecimal("100.00"));
        savedEvent.setCurrency("USD");
        savedEvent.setStatus(EventStatus.PROCESSED);
        savedEvent.setEventTimestamp(request.getEventTimestamp());

        when(repository.save(any(Event.class)))
                .thenReturn(savedEvent);

        EventResponse response =
                service.createEvent(request);

        assertEquals(
                "evt-001",
                response.getEventId()
        );

        assertEquals(
                EventStatus.PROCESSED,
                response.getStatus()
        );

        verify(repository, times(1))
                .save(any(Event.class));
    }

    @Test
    void shouldReturnExistingEventForDuplicateId()
            throws Exception {

        Event existing = new Event();

        existing.setId(1L);
        existing.setEventId("evt-001");
        existing.setAccountId("acct-123");
        existing.setType(EventType.CREDIT);
        existing.setAmount(new BigDecimal("100.00"));
        existing.setCurrency("USD");
        existing.setStatus(EventStatus.PROCESSED);
        existing.setEventTimestamp(Instant.now());

        when(repository.findByEventId("evt-001"))
                .thenReturn(Optional.of(existing));

        EventRequest request =
                new EventRequest();

        request.setEventId("evt-001");

        EventResponse response =
                service.createEvent(request);

        assertEquals(
                "evt-001",
                response.getEventId()
        );

        verify(repository, never())
                .save(any(Event.class));
    }

    @Test
    void shouldReturnEventsOrderedByTimestamp() {

        Instant older =
                Instant.parse(
                        "2026-05-15T14:00:00Z"
                );

        Instant newer =
                Instant.parse(
                        "2026-05-15T15:00:00Z"
                );

        Event first = new Event();

        first.setEventId("evt-001");
        first.setAccountId("acct-123");
        first.setType(EventType.CREDIT);
        first.setAmount(new BigDecimal("100"));
        first.setCurrency("USD");
        first.setStatus(EventStatus.PROCESSED);
        first.setEventTimestamp(older);

        Event second = new Event();

        second.setEventId("evt-002");
        second.setAccountId("acct-123");
        second.setType(EventType.DEBIT);
        second.setAmount(new BigDecimal("20"));
        second.setCurrency("USD");
        second.setStatus(EventStatus.PROCESSED);
        second.setEventTimestamp(newer);

        when(repository
                .findByAccountIdOrderByEventTimestampAsc(
                        "acct-123"
                ))
                .thenReturn(
                        List.of(first, second)
                );

        List<EventResponse> responses =
                service.getEventsByAccount(
                        "acct-123"
                );

        assertEquals(
                "evt-001",
                responses.get(0).getEventId()
        );

        assertEquals(
                "evt-002",
                responses.get(1).getEventId()
        );
    }
}