package com.klaxon.diary.controller.api;

import com.klaxon.diary.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleController {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 * * * * ?")
    private void deleteExpiredRefreshTokens() {
        refreshTokenRepository.deleteExpired();
    }
}
