package com.klaxon.diary.dto;

import java.time.Instant;

public record RefreshToken(User user, String token, String deviceId, Instant expiryDate) {}
