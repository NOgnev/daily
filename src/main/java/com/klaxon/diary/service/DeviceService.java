package com.klaxon.diary.service;

import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RefreshTokenRepository refreshTokenRepository;


    public void revokeDevice(UUID userId, UUID deviceId) {
        refreshTokenRepository.delete(userId, deviceId);
    }

    public List<RefreshToken> getDevices(UUID userId) {
        return refreshTokenRepository.findAllByUserId(userId);
    }
}
