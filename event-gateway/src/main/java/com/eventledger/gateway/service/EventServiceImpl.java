package com.eventledger.gateway.service;

import com.eventledger.gateway.dto.EventRequest;
import com.eventledger.gateway.dto.EventResponse;
import com.eventledger.gateway.entity.Event;
import com.eventledger.gateway.entity.EventStatus;
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

    @Override
    public EventResponse createEvent(
            EventRequest request
    ) {

        Optional<Event> existing =
                repository.findByEventId(
                        request.getEventId()
                );

        if (existing.isPresent()) {

            Event event = existing.get();

            return map(event);
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

        event.setStatus(
                EventStatus.PROCESSED
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
}
