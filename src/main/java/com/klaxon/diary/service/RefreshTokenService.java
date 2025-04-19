package com.klaxon.diary.service;

import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.dto.User;
import com.klaxon.diary.repository.RefreshTokenRepository;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;
    @Value("${jwt.max-devices-count}")
    private int maxDevicesCount;

    public RefreshToken createRefreshToken(String username, UUID deviceId) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<RefreshToken> allByUserId = refreshTokenRepository.findAllByUserId(user.id());
        if (allByUserId.size() > maxDevicesCount) {
            throw new RuntimeException("Too many refresh tokens");
        }
        allByUserId.stream()
                .filter(refreshToken -> refreshToken.deviceId().equals(deviceId))
                .findFirst()
                .ifPresent(token -> refreshTokenRepository.delete(token.userId(), token.deviceId()));

        RefreshToken token = new RefreshToken(
                user.id(), UUID.randomUUID().toString(), deviceId, Instant.now().plusMillis(jwtRefreshExpirationMs));

        return refreshTokenRepository.save(token);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenRepository.delete(refreshToken.userId(), refreshToken.deviceId());

        if (refreshToken.expiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }

    public void revokeDevice(String username, UUID deviceId) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.delete(user.id(), deviceId);
    }

    public List<RefreshToken> getDevices(String username) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return refreshTokenRepository.findAllByUserId(user.id());
    }
}
