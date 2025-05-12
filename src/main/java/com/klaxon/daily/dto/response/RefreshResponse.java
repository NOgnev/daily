package com.klaxon.daily.dto.response;

import com.klaxon.daily.config.log.hidden.Hidden;
import com.klaxon.daily.dto.RefreshToken;

public record RefreshResponse(@Hidden String accessToken, RefreshToken refreshToken) {}
