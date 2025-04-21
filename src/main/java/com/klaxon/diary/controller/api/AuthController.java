package com.klaxon.diary.controller.api;

import com.klaxon.diary.config.log.Log;
import com.klaxon.diary.dto.request.AuthRequest;
import com.klaxon.diary.dto.request.RefreshTokenRequest;
import com.klaxon.diary.dto.response.TokensResponse;
import com.klaxon.diary.service.AuthService;
import com.klaxon.diary.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.klaxon.diary.util.Headers.DEVICE_ID_HEADER;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Log
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AuthRequest request) {
        authService.register(request.nickname(), request.password());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokensResponse> login(@RequestHeader(DEVICE_ID_HEADER) UUID deviceId,
                                                @RequestBody AuthRequest request) {
        return ResponseEntity.ok().body(authService.login(request, deviceId));
    }

    @Log
    @PostMapping("/refresh")
    public ResponseEntity<TokensResponse> refresh(@RequestHeader(DEVICE_ID_HEADER) UUID deviceId,
                                                  @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok().body(refreshTokenService.refresh(request.refreshToken(), deviceId));
    }
}
