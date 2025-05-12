package com.klaxon.daily.service;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.RefreshToken;
import com.klaxon.daily.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Log
    @Transactional
    public void revokeDevice(UUID userId, UUID deviceId) {
        refreshTokenRepository.delete(userId, deviceId);
    }

    @Log
    @Transactional(readOnly = true)
    public List<RefreshToken> getDevices(UUID userId) {
        return refreshTokenRepository.findAllByUserId(userId);
    }
}
