package com.klaxon.diary.dto;

import com.klaxon.diary.config.log.hidden.Hidden;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RefreshToken(UUID userId, @Hidden String token, Device device) {
    @Builder
    public record Device(UUID id, Instant expiryDate) {}
}
