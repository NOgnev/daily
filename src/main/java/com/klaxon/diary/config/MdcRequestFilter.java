package com.klaxon.diary.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.klaxon.diary.util.Headers.SERVER_TIME_HEADER;
import static com.klaxon.diary.util.Headers.TRACE_ID_HEADER;
import static com.klaxon.diary.util.MdcKey.TRACE_ID;

@Component
@Order(Integer.MIN_VALUE)
public class MdcRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);
        populateResponseWithHeaders(response);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private void populateResponseWithHeaders(HttpServletResponse response) {
        response.addHeader(TRACE_ID_HEADER, MDC.get(TRACE_ID));
        response.addHeader(SERVER_TIME_HEADER, OffsetDateTime.now().truncatedTo(ChronoUnit.MILLIS).toString());
    }
}
