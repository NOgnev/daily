package com.klaxon.daily.service;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.config.log.hidden.Hidden;
import com.klaxon.daily.config.security.JwtProvider;
import com.klaxon.daily.dto.RefreshToken;
import com.klaxon.daily.dto.response.RefreshResponse;
import com.klaxon.daily.error.AppException;
import com.klaxon.daily.error.ErrorRegistry;
import com.klaxon.daily.repository.RefreshTokenRepository;
import com.klaxon.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.klaxon.daily.util.MdcKey.USER_ID;

@Slf4j
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

    @Log
    @Transactional
    public RefreshResponse refresh(@Hidden String requestToken) {
        if (requestToken == null) {
            throw AppException.builder()
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                    .args(Map.of("message", "Refresh token is required"))
                    .build();
        }

        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByToken(requestToken);
        var refreshToken = refreshTokens.stream()
                .filter(rt -> rt.token().equals(requestToken))
                .findFirst()
                .orElseThrow(() -> AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                        .args(Map.of("message", "Refresh token not found"))
                        .build());
        MDC.put(USER_ID, refreshToken.userId().toString());

        if (refreshToken.device().expiryDate().isBefore(Instant.now())) {
            throw AppException.builder()
                    .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                    .error(ErrorRegistry.REFRESH_TOKEN_INVALID)
                    .args(Map.of("message", "Refresh token expired"))
                    .build();}

        var userDetails = userRepository.findById(refreshToken.userId())
                .orElseThrow(() -> AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.USER_NOT_FOUND)
                        .args(Map.of("userId", refreshToken.userId().toString()))
                        .build());

        var newRefreshToken = createRefreshToken(refreshToken.userId(), refreshToken.device().id(), refreshTokens);
        var newAccessToken = jwtProvider.generateAccessToken(userDetails);

        return new RefreshResponse(newAccessToken, newRefreshToken);
    }

    @Log
    @Transactional
    public RefreshToken createRefreshToken(UUID userId, UUID deviceId) {
        return createRefreshToken(userId, deviceId, refreshTokenRepository.findAllByUserId(userId));
    }

    @Log
    private RefreshToken createRefreshToken(UUID userId, UUID deviceId, List<RefreshToken> tokens) {
        int tokenSize = tokens.size();
        Optional<RefreshToken> expectedToken = tokens.stream()
                .filter(refreshToken -> refreshToken.device().id().equals(deviceId))
                .findFirst();
        if (expectedToken.isPresent()) {
            refreshTokenRepository.delete(expectedToken.get().userId(), expectedToken.get().device().id());
            tokenSize--;
        }
        if (tokenSize >= maxDevicesCount) {
            log.warn("Too many refresh tokens");
            var oldestToken = tokens.stream()
                    .min(Comparator.comparing(refreshToken -> refreshToken.device().expiryDate()))
                    .get();
            refreshTokenRepository.delete(oldestToken.userId(), oldestToken.device().id());
        }

        RefreshToken token = new RefreshToken(userId, UUID.randomUUID().toString(),
                new RefreshToken.Device(deviceId, Instant.now().plusMillis(jwtRefreshExpirationMs)));

        return refreshTokenRepository.save(token);
    }

    @Log
    public void deleteRefreshToken(@Hidden String token) {
        refreshTokenRepository.delete(token);
    }
}
