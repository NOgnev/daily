package com.klaxon.diary.dto.response;

import com.klaxon.diary.config.log.hidden.Hidden;
import com.klaxon.diary.dto.RefreshToken;

public record RefreshResponse(@Hidden String accessToken, RefreshToken refreshToken) {}
