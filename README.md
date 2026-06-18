# Event Ledger

## Overview

Event Ledger is a distributed event processing system built using Spring Boot and Java 21. The system processes financial transaction events received from upstream systems and maintains account balances while handling duplicate events, out-of-order event delivery, and service failures.

The solution consists of two independently deployable microservices:

### Event Gateway API

The public-facing service responsible for:

* Receiving transaction events
* Request validation
* Event persistence
* Idempotency handling
* Event retrieval and listing
* Trace propagation
* Communication with Account Service

### Account Service

Internal service responsible for:

* Account management
* Transaction persistence
* Balance computation
* Transaction history retrieval

Both services maintain separate H2 databases and communicate synchronously using REST APIs.

---

## Architecture

Client
|
v
Event Gateway (Port 8080)
|
v
Account Service (Port 8081)

### Service Responsibilities

#### Event Gateway

* POST /events
* GET /events/{eventId}
* GET /events?account={accountId}
* Trace generation and propagation
* Event persistence
* Idempotency checks

#### Account Service

* POST /accounts/{accountId}/transactions
* GET /accounts/{accountId}/balance
* GET /accounts/{accountId}
* Balance calculation
* Transaction persistence

---

## Technology Stack

* Java 21
* Spring Boot 3.5.x
* Spring Data JPA
* H2 Database
* Spring Validation
* Spring Actuator
* Lombok
* RestClient
* Resilience4j
* JUnit 5
* Mockito
* Maven

---

## Implemented Features

### Idempotency

Duplicate event submissions are detected using the unique eventId.

If an event already exists:

* No duplicate record is created
* Account balance is not modified again
* Existing event information is returned

---

### Out-of-Order Event Handling

Events may arrive out of chronological order.

Event listings are returned ordered by:

eventTimestamp ASC

Balance calculations remain correct regardless of arrival order.

---

### Balance Computation

Balance is calculated as:

Balance = Credits - Debits

Examples:

CREDIT 100
DEBIT 40

Balance = 60

---

### Validation

Request validation includes:

* Required fields
* Positive amount validation
* Supported transaction types
* Proper timestamp validation

Invalid requests return HTTP 400.

---

### Distributed Tracing

Gateway generates or accepts:

X-Trace-Id

The trace ID is:

* Stored in request context
* Propagated to Account Service
* Included in service logs
* Returned in response headers

This allows tracking a request across both services.

---

### Graceful Degradation

When Account Service is unavailable:

POST /events

Returns:

HTTP 503 Service Unavailable

Event status is stored as FAILED.

Gateway event retrieval endpoints continue to function because they rely on Gateway-local persistence.

---

### Resiliency

Resilience4j Circuit Breaker is implemented on Gateway calls to Account Service.

The circuit breaker:

* Monitors failures
* Opens after configured failure thresholds
* Prevents repeated calls to unavailable services
* Recovers automatically after configured wait duration

---

## Database Schema

### Event Gateway

EVENTS

* id
* event_id
* account_id
* type
* amount
* currency
* event_timestamp
* metadata_json
* status
* created_at

---

### Account Service

ACCOUNTS

* account_id
* balance
* created_at
* updated_at

ACCOUNT_TRANSACTIONS

* id
* event_id
* account_id
* type
* amount
* currency
* event_timestamp
* created_at

---

## Running the Services

### Account Service

cd account-service

mvn spring-boot:run

Runs on:

http://localhost:8081

---

### Event Gateway

cd event-gateway

mvn spring-boot:run

Runs on:

http://localhost:8080

---

## Health Endpoints

Gateway

GET /actuator/health

Account Service

GET /actuator/health

---

## Example Request

POST /events

Request:

{
"eventId": "evt-001",
"accountId": "acct-123",
"type": "CREDIT",
"amount": 150.00,
"currency": "USD",
"eventTimestamp": "2026-05-15T14:02:11Z",
"metadata": {
"source": "mainframe-batch"
}
}

Response:

{
"eventId": "evt-001",
"accountId": "acct-123",
"status": "PROCESSED"
}

---

## Testing

Execute:

mvn test

Current automated tests cover:

* Repository persistence
* Event creation
* Idempotency validation
* Balance computation
* Duplicate transaction protection
* Event ordering

---

## Future Enhancements

The following items are currently in progress:

* Integration tests (Gateway → Account Service)
* Trace propagation integration tests
* Circuit breaker integration tests
* Micrometer custom metrics
* Structured JSON logging
* Docker Compose setup

---

## Design Decisions

### Why BigDecimal?

Financial calculations require precise decimal arithmetic.

BigDecimal avoids floating-point rounding issues that occur with double.

### Why Separate Databases?

The assignment requires complete service isolation.

Each service owns its data and communicates only through REST APIs.

### Why Circuit Breaker?

Circuit Breaker prevents repeated calls to a failing dependency, improving response times and protecting system resources during outages.

### Why Event Ordering by Timestamp?

Events may arrive out of order.

Ordering by eventTimestamp ensures clients always see the correct chronological history.
