package com.eventledger.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerLogger {

    private final CircuitBreakerRegistry registry;

    @PostConstruct
    public void init() {

        registry.circuitBreaker(
                        "accountService"
                )
                .getEventPublisher()
                .onStateTransition(
                        event -> log.warn(
                                "Circuit state changed {}",
                                event.getStateTransition()
                        )
                );
    }
}