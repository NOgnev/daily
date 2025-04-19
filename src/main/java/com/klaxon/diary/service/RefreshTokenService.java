package com.klaxon.diary.service;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.dto.AuthUser;
import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.dto.response.TokensResponse;
import com.klaxon.diary.repository.RefreshTokenRepository;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.klaxon.diary.util.MdcKey.USER_ID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;
    @Value("${jwt.max-devices-count}")
    private int maxDevicesCount;

    public TokensResponse refresh(String requestToken, UUID deviceId) {
        if (requestToken == null) {
            throw new RuntimeException("Refresh token is required");
        }

        RefreshToken refreshToken = validateRefreshToken(requestToken);

        if (!refreshToken.device().id().equals(deviceId)) {
            throw new RuntimeException("Invalid device id");
        }

        var userDetails = userRepository.findById(refreshToken.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var newRefreshToken = createRefreshToken(userDetails, deviceId).token();
        var newAccessToken = jwtProvider.generateAccessToken(userDetails);

        return new TokensResponse(newAccessToken, newRefreshToken);
    }

    public RefreshToken createRefreshToken(AuthUser user, UUID deviceId) {
        List<RefreshToken> allByUserId = refreshTokenRepository.findAllByUserId(user.id());
        if (allByUserId.size() > maxDevicesCount) {
            throw new RuntimeException("Too many refresh tokens");
        }
        allByUserId.stream()
                .filter(refreshToken -> refreshToken.device().id().equals(deviceId))
                .findFirst()
                .ifPresent(token -> refreshTokenRepository.delete(token.userId(), token.device().id()));

        RefreshToken token = new RefreshToken(user.id(), UUID.randomUUID().toString(),
                new RefreshToken.Device(deviceId, Instant.now().plusMillis(jwtRefreshExpirationMs)));

        return refreshTokenRepository.save(token);
    }

    private RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
        MDC.put(USER_ID, refreshToken.userId().toString());

        refreshTokenRepository.delete(refreshToken.userId(), refreshToken.device().id());

        if (refreshToken.device().expiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return refreshToken;
    }
}
