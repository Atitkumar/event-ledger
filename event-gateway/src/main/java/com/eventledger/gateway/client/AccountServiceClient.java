package com.eventledger.gateway.client;

import com.eventledger.gateway.client.dto.ApplyTransactionRequest;
import com.eventledger.gateway.client.dto.BalanceResponse;
import com.eventledger.gateway.exception.AccountServiceUnavailableException;
import com.eventledger.gateway.tracing.TraceContext;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class AccountServiceClient {

    private final RestClient restClient;

    @CircuitBreaker(
            name = "accountService",
            fallbackMethod =
                    "applyTransactionFallback"
    )
    public BalanceResponse applyTransaction(
            String accountId,
            ApplyTransactionRequest request
    ) {

        return restClient.post()
                .uri(
                        "/accounts/{accountId}/transactions",
                        accountId
                )
                .header(
                        "X-Trace-Id",
                        TraceContext.getTraceId()
                )
                .body(request)
                .retrieve()
                .body(BalanceResponse.class);
    }

    public BalanceResponse applyTransactionFallback(
            String accountId,
            ApplyTransactionRequest request,
            Exception ex
    ) {

        throw new AccountServiceUnavailableException(
                "Account Service unavailable"
        );
    }
}