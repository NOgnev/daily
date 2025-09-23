package com.klaxon.daily.controller;

import com.klaxon.daily.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.klaxon.daily.util.MdcKey.OPERATION_NAME;
import static com.klaxon.daily.util.MdcKey.TRACE_ID;

@Component
@RequiredArgsConstructor
public class ScheduleController {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * ?")
    private void deleteExpiredRefreshTokens() {
        MDC.put(TRACE_ID, UUID.randomUUID().toString());
        MDC.put(OPERATION_NAME, "deleteExpiredRefreshTokens");
        refreshTokenRepository.deleteExpired();
    }
}
