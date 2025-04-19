package com.klaxon.diary.service;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.dto.request.AuthRequest;
import com.klaxon.diary.dto.AuthUser;
import com.klaxon.diary.dto.response.TokensResponse;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.klaxon.diary.util.MdcKey.USER_ID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthUser register(String nickname, String password) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new RuntimeException("User with nickname " + nickname + " already exists");
        }
        UUID userId = UUID.randomUUID();
        MDC.put(USER_ID, userId.toString());
        return userRepository.save(new AuthUser(userId, nickname, passwordEncoder.encode(password)));
    }

    public TokensResponse login(AuthRequest request, UUID deviceId) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nickname(), request.password())
        );

        AuthUser user = (AuthUser) auth.getPrincipal();
        MDC.put(USER_ID, user.id().toString());
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user, deviceId).token();

        return new TokensResponse(accessToken, refreshToken);
    }
}
