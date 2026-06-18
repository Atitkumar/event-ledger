package com.eventledger.gateway.controller;

import com.eventledger.gateway.dto.EventRequest;
import com.eventledger.gateway.dto.EventResponse;
import com.eventledger.gateway.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @PostMapping
    public EventResponse createEvent(
            @Valid
            @RequestBody
            EventRequest request
    ) {

        return service.createEvent(
                request
        );
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(
            @PathVariable String eventId
    ) {

        return service.getEvent(
                eventId
        );
    }

    @GetMapping
    public List<EventResponse> getEvents(
            @RequestParam String account
    ) {

        return service.getEventsByAccount(
                account
        );
    }
}