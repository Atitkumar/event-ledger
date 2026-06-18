package com.eventledger.accountservice.tracing;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

        String traceId =
                httpRequest.getHeader(
                        "X-Trace-Id"
                );

        if (traceId != null) {

            TraceContext.setTraceId(traceId);

            MDC.put(
                    "traceId",
                    traceId
            );
        }

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