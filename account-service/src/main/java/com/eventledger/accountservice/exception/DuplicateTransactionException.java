package com.eventledger.accountservice.exception;

public class DuplicateTransactionException
        extends RuntimeException {

    public DuplicateTransactionException(String eventId) {
        super("Duplicate transaction event: " + eventId);
    }
}