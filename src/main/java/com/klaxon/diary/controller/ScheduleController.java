package com.klaxon.diary.controller;

import com.klaxon.diary.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.klaxon.diary.util.MdcKey.TRACE_ID;

@Component
@RequiredArgsConstructor
public class ScheduleController {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 * * * ?")
    private void deleteExpiredRefreshTokens() {
        MDC.put(TRACE_ID, UUID.randomUUID().toString());
        refreshTokenRepository.deleteExpired();
    }
}
