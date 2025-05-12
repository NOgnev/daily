package com.klaxon.daily.service;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.config.log.hidden.Hidden;
import com.klaxon.daily.config.security.JwtProvider;
import com.klaxon.daily.dto.AuthUser;
import com.klaxon.daily.dto.request.LoginRequest;
import com.klaxon.daily.dto.response.LoginResponse;
import com.klaxon.daily.error.AppException;
import com.klaxon.daily.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.klaxon.daily.error.ErrorRegistry.USER_ALREADY_EXISTS;
import static com.klaxon.daily.util.MdcKey.USER_ID;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Log
    public AuthUser register(String nickname, @Hidden String password) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw AppException.builder()
                    .httpStatus(UNPROCESSABLE_ENTITY)
                    .error(USER_ALREADY_EXISTS)
                    .args(Map.of("nickname", nickname))
                    .build();
        }
        UUID userId = UUID.randomUUID();
        MDC.put(USER_ID, userId.toString());
        return userRepository.save(AuthUser.builder()
                .id(userId)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .build());
    }

    @Log
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nickname(), request.password())
        );
        AuthUser user = (AuthUser) auth.getPrincipal();
        MDC.put(USER_ID, user.id().toString());
        String accessToken = jwtProvider.generateAccessToken(user);
        UUID deviceId = UUID.randomUUID();
        String refreshToken = refreshTokenService.createRefreshToken(user.id(), deviceId).token();
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceId(deviceId)
                .user(LoginResponse.User.builder()
                        .id(user.id())
                        .nickname(user.nickname())
                        .build())
                .build();
    }
}
