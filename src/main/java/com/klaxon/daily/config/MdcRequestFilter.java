package com.klaxon.daily.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static com.klaxon.daily.util.Constants.SERVER_TIME_HEADER;
import static com.klaxon.daily.util.Constants.TRACE_ID_HEADER;
import static com.klaxon.daily.util.MdcKey.OPERATION_NAME;
import static com.klaxon.daily.util.MdcKey.TRACE_ID;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Integer.MIN_VALUE)
public class MdcRequestFilter extends OncePerRequestFilter {

    private final RequestMappingHandlerMapping requestHandlerMapping;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);
        populateResponseWithHeaders(response);
        getOperationName(request).ifPresentOrElse(v -> {
                    MDC.put(OPERATION_NAME, v);
                    log.info("[traceId={}] Request {} with operation name {}", traceId, request.getRequestURI(), v);
                },
                () -> log.warn("[traceId={}] Could not determine operation name for request: {}", traceId, request.getRequestURI()));
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

    private Optional<String> getOperationName(HttpServletRequest request) {
        try {
            return ofNullable(requestHandlerMapping.getHandler(request))
                    .map(mapping -> ((HandlerMethod) mapping.getHandler()).getMethod())
                    .map(Method::getName);
        } catch (Exception e) {
            return empty();
        }
    }
}
