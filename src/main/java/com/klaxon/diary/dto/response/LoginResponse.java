package com.klaxon.diary.dto.response;

import com.klaxon.diary.config.log.hidden.Hidden;
import lombok.Builder;

import java.util.UUID;

@Builder
public record LoginResponse(@Hidden String accessToken, @Hidden String refreshToken, UUID deviceId, User user) {

    @Builder
    public record User(UUID id, String nickname) {}
}
