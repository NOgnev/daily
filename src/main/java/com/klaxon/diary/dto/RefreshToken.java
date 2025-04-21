package com.klaxon.diary.dto;

import com.klaxon.diary.config.log.hidden.Hidden;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(UUID userId, @Hidden String token, Device device) {
    public record Device(UUID id, Instant expiryDate) {}
}
