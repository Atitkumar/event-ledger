package com.eventledger.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            DuplicateTransactionException.class
    )
    public ResponseEntity<?> handleDuplicate(
            DuplicateTransactionException ex
    ) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "timestamp", Instant.now(),
                                "message", ex.getMessage()
                        )
                );
    }

    @ExceptionHandler(
            AccountNotFoundException.class
    )
    public ResponseEntity<?> handleNotFound(
            AccountNotFoundException ex
    ) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of(
                                "timestamp", Instant.now(),
                                "message", ex.getMessage()
                        )
                );
    }
}