package com.klaxon.diary.dto;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(UUID userId, String token, Device device) {
    public record Device(UUID id, Instant expiryDate) {}
}
