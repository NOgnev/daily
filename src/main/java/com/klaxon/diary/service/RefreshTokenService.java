package com.klaxon.diary.service;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.dto.response.TokensResponse;
import com.klaxon.diary.error.AppException;
import com.klaxon.diary.error.ErrorRegistry;
import com.klaxon.diary.repository.RefreshTokenRepository;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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

    @Transactional
    public TokensResponse refresh(String requestToken, UUID deviceId) {
        if (requestToken == null) {
            throw AppException.builder()
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                    .args(Map.of("message", "Refresh token is required"))
                    .build();
        }

        RefreshToken refreshToken = validateRefreshToken(requestToken);

        if (!refreshToken.device().id().equals(deviceId)) {
            throw AppException.builder()
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .args(Map.of("message", "Invalid device id"))
                    .build();
        }

        var userDetails = userRepository.findById(refreshToken.userId())
                .orElseThrow(() -> AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.USER_NOT_FOUND)
                        .args(Map.of("userId", refreshToken.userId().toString()))
                        .build());

        var newRefreshToken = createRefreshToken(refreshToken.userId(), deviceId).token();
        var newAccessToken = jwtProvider.generateAccessToken(userDetails);

        return new TokensResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public RefreshToken createRefreshToken(UUID userId, UUID deviceId) {
        List<RefreshToken> allByUserId = refreshTokenRepository.findAllByUserId(userId);
        if (allByUserId.size() > maxDevicesCount) {
            throw AppException.builder()
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                    .args(Map.of("message", "Too many refresh tokens"))
                    .build();
        }
        allByUserId.stream()
                .filter(refreshToken -> refreshToken.device().id().equals(deviceId))
                .findFirst()
                .ifPresent(token -> refreshTokenRepository.delete(token.userId(), token.device().id()));

        RefreshToken token = new RefreshToken(userId, UUID.randomUUID().toString(),
                new RefreshToken.Device(deviceId, Instant.now().plusMillis(jwtRefreshExpirationMs)));

        return refreshTokenRepository.save(token);
    }

    @Transactional
    private RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                        .args(Map.of("message", "Refresh token not found"))
                        .build());
        MDC.put(USER_ID, refreshToken.userId().toString());

        refreshTokenRepository.delete(refreshToken.userId(), refreshToken.device().id());

        if (refreshToken.device().expiryDate().isBefore(Instant.now())) {
            throw AppException.builder()
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                    .args(Map.of("message", "Refresh token expired"))
                    .build();}

        return refreshToken;
    }
}
