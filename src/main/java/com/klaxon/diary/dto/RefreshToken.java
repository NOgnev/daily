package com.klaxon.diary.dto;

import java.time.Instant;
import java.util.UUID;

public record RefreshToken(UUID userId, String token, String deviceId, Instant expiryDate) {}
