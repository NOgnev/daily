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

    public RefreshToken createRefreshToken(String username, String deviceId) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);

        RefreshToken token = new RefreshToken(
                user, UUID.randomUUID().toString(), deviceId, Instant.now().plusMillis(jwtRefreshExpirationMs));

        return refreshTokenRepository.save(token);
    }

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.expiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired");
        }

        refreshTokenRepository.delete(refreshToken);

        return refreshToken;
    }

    public void revokeDevice(String username, String deviceId) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenRepository.deleteByUserAndDeviceId(user, deviceId);
    }

    public List<RefreshToken> getDevices(String username) {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return refreshTokenRepository.findAllByUser(user);
    }
}
