package com.klaxon.diary.dto.request;

import com.klaxon.diary.config.log.hidden.Hidden;

public record RefreshTokenRequest(@Hidden String refreshToken) {
}
