package com.klaxon.diary.dto.response;

import com.klaxon.diary.config.log.hidden.Hidden;

public record RefreshResponse(@Hidden String accessToken, @Hidden String refreshToken) {}
