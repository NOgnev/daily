package com.klaxon.diary.repository;

import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.dto.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class RefreshTokenRepository {
    public static Set<RefreshToken> TOKENS = new HashSet<>();


    public List<RefreshToken> findAllByUser(User user) {
        return TOKENS.stream().filter(t -> t.user().id().equals(user.id())).toList();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return TOKENS.stream()
                .filter(t -> t.token().equals(token))
                .findFirst();
    }

    public RefreshToken save(RefreshToken token) {
        TOKENS.add(token);
        return token;
    }

    public void delete(RefreshToken refreshToken) {
        TOKENS.removeIf(t -> t.token().equals(refreshToken.token()));
    }

    public void deleteByUserAndDeviceId(User user, String deviceId) {
        TOKENS.removeIf(t -> t.user().id().equals(user.id())
                && t.deviceId().equals(deviceId));
    }
}
