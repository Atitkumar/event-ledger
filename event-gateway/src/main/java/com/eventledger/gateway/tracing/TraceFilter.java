package com.eventledger.gateway.tracing;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class TraceFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String traceId =
                httpRequest.getHeader("X-Trace-Id");

        if (traceId == null || traceId.isBlank()) {

            traceId = UUID.randomUUID()
                    .toString();
        }

        TraceContext.setTraceId(traceId);

        MDC.put("traceId", traceId);

        httpResponse.setHeader(
                "X-Trace-Id",
                traceId
        );

        try {

            chain.doFilter(
                    request,
                    response
            );

        } finally {

            MDC.clear();

            TraceContext.clear();
        }
    }
}