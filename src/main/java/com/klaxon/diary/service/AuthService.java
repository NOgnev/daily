package com.klaxon.diary.service;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.dto.AuthRequest;
import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.dto.TokensHolder;
import com.klaxon.diary.dto.User;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public User register(String nickname, String password) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new RuntimeException("User with nickname " + nickname + " already exists");
        }

        return userRepository.save(new User(UUID.randomUUID(), nickname, passwordEncoder.encode(password)));
    }

    public TokensHolder login(AuthRequest request, UUID deviceId) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nickname(), request.password())
        );

        String accessToken = jwtProvider.generateAccessToken(auth);
        String refreshToken = refreshTokenService.createRefreshToken(auth.getName(), deviceId).token();

        return new TokensHolder(accessToken, refreshToken);
    }

    public TokensHolder refresh(String requestToken, UUID deviceId) {
        if (requestToken == null) {
            throw new RuntimeException("Refresh token is required");
        }

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(requestToken);

        if (!refreshToken.deviceId().equals(deviceId)) {
            throw new RuntimeException("Invalid device id");
        }
        var userDetails = userRepository.findById(refreshToken.userId())
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.nickname())
                        .password(user.password())
                        .authorities("USER")
                        .build())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        var newAccessToken = jwtProvider.generateAccessToken(authToken);
        var newRefreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername(), deviceId).token();

        return new TokensHolder(newAccessToken, newRefreshToken);
    }
}
