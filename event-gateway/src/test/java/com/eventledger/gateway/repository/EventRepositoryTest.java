package com.eventledger.gateway.repository;

import com.eventledger.gateway.entity.Event;
import com.eventledger.gateway.entity.EventStatus;
import com.eventledger.gateway.entity.EventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EventRepositoryTest {

    @Autowired
    private EventRepository repository;

    @Test
    void shouldSaveEvent() {

        Event event = new Event();

        event.setEventId("evt-001");
        event.setAccountId("acct-123");
        event.setType(EventType.CREDIT);
        event.setAmount(
                new BigDecimal("100.00")
        );
        event.setCurrency("USD");
        event.setEventTimestamp(
                Instant.now()
        );
        event.setStatus(
                EventStatus.PROCESSED
        );

        Event saved =
                repository.save(event);

        assertThat(saved.getId())
                .isNotNull();

        assertThat(saved.getEventId())
                .isEqualTo("evt-001");
    }
}