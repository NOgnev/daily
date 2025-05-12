package com.klaxon.daily.dto;

import com.klaxon.daily.config.log.hidden.Hidden;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RefreshToken(UUID userId, @Hidden String token, Device device) {
    @Builder
    public record Device(UUID id, Instant expiryDate) {}
}
