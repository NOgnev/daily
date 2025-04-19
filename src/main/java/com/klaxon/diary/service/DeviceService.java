package com.klaxon.diary.service;

import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RefreshTokenRepository refreshTokenRepository;


    @Transactional
    public void revokeDevice(UUID userId, UUID deviceId) {
        refreshTokenRepository.delete(userId, deviceId);
    }

    @Transactional(readOnly = true)
    public List<RefreshToken> getDevices(UUID userId) {
        return refreshTokenRepository.findAllByUserId(userId);
    }
}
