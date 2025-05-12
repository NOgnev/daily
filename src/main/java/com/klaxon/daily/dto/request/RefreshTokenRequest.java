package com.klaxon.daily.dto.request;

import com.klaxon.daily.config.log.hidden.Hidden;

public record RefreshTokenRequest(@Hidden String refreshToken) {
}
