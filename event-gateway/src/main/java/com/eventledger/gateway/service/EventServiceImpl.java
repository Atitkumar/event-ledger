package com.eventledger.gateway.service;

import com.eventledger.gateway.client.AccountServiceClient;
import com.eventledger.gateway.client.dto.ApplyTransactionRequest;
import com.eventledger.gateway.dto.EventRequest;
import com.eventledger.gateway.dto.EventResponse;
import com.eventledger.gateway.entity.Event;
import com.eventledger.gateway.entity.EventStatus;
import com.eventledger.gateway.exception.AccountServiceUnavailableException;
import com.eventledger.gateway.exception.EventNotFoundException;
import com.eventledger.gateway.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl
        implements EventService {

    private final EventRepository repository;

    private final ObjectMapper objectMapper;

    private final AccountServiceClient accountServiceClient;

    @Override
    public EventResponse createEvent(
            EventRequest request
    ) {

        Optional<Event> existing =
                repository.findByEventId(
                        request.getEventId()
                );

        if (existing.isPresent()) {

            return map(
                    existing.get()
            );
        }

        Event event = new Event();

        event.setEventId(
                request.getEventId()
        );

        event.setAccountId(
                request.getAccountId()
        );

        event.setType(
                request.getType()
        );

        event.setAmount(
                request.getAmount()
        );

        event.setCurrency(
                request.getCurrency()
        );

        event.setEventTimestamp(
                request.getEventTimestamp()
        );

        try {

            event.setMetadataJson(
                    objectMapper.writeValueAsString(
                            request.getMetadata()
                    )
            );

        } catch (Exception ex) {

            throw new RuntimeException(ex);
        }

        try {

            accountServiceClient.applyTransaction(
                    request.getAccountId(),
                    buildAccountRequest(
                            request
                    )
            );

            event.setStatus(
                    EventStatus.PROCESSED
            );

        } catch (Exception ex) {

            event.setStatus(
                    EventStatus.FAILED
            );

            repository.save(event);

            throw new AccountServiceUnavailableException(
                    "Account Service unavailable"
            );
        }

        Event saved =
                repository.save(event);

        return map(saved);
    }

    @Override
    public EventResponse getEvent(
            String eventId
    ) {

        Event event =
                repository.findByEventId(eventId)
                        .orElseThrow(
                                () ->
                                        new EventNotFoundException(
                                                eventId
                                        )
                        );

        return map(event);
    }

    @Override
    public List<EventResponse> getEventsByAccount(
            String accountId
    ) {

        return repository
                .findByAccountIdOrderByEventTimestampAsc(
                        accountId
                )
                .stream()
                .map(this::map)
                .toList();
    }

    private EventResponse map(
            Event event
    ) {

        return new EventResponse(
                event.getEventId(),
                event.getAccountId(),
                event.getType(),
                event.getAmount(),
                event.getCurrency(),
                event.getEventTimestamp(),
                event.getStatus()
        );
    }
    private ApplyTransactionRequest buildAccountRequest(
            EventRequest request
    ) {

        ApplyTransactionRequest accountRequest =
                new ApplyTransactionRequest();

        accountRequest.setEventId(
                request.getEventId()
        );

        accountRequest.setType(
                request.getType()
        );

        accountRequest.setAmount(
                request.getAmount()
        );

        accountRequest.setCurrency(
                request.getCurrency()
        );

        accountRequest.setEventTimestamp(
                request.getEventTimestamp()
        );

        return accountRequest;
    }
}
