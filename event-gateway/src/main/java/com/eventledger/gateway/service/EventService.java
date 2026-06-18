package com.eventledger.gateway.service;

import com.eventledger.gateway.dto.EventRequest;
import com.eventledger.gateway.dto.EventResponse;

import java.util.List;

public interface EventService {

    EventResponse createEvent(
            EventRequest request
    );

    EventResponse getEvent(
            String eventId
    );

    List<EventResponse> getEventsByAccount(
            String accountId
    );
}