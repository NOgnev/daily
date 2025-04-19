package com.klaxon.diary.dto;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(UUID userId, String token, UUID deviceId, Instant expiryDate) {}
