package com.klaxon.diary.controller.api;

import com.klaxon.diary.config.log.Log;
import com.klaxon.diary.config.log.hidden.Hidden;
import com.klaxon.diary.dto.request.AuthRequest;
import com.klaxon.diary.dto.response.LoginResponse;
import com.klaxon.diary.dto.response.RefreshResponse;
import com.klaxon.diary.service.AuthService;
import com.klaxon.diary.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.klaxon.diary.service.CookieService.attachCookie;
import static com.klaxon.diary.util.Constants.ACCESS_TOKEN_COOKIE;
import static com.klaxon.diary.util.Constants.DEVICE_ID_COOKIE;
import static com.klaxon.diary.util.Constants.REFRESH_TOKEN_COOKIE;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.access-expiration-ms}")
    private long jwtAccessExpirationMs;
    @Value("${jwt.refresh-expiration-ms}")
    private long jwtRefreshExpirationMs;

    @Log
    @PostMapping("/register")
    public ResponseEntity<LoginResponse.User> register(@RequestBody AuthRequest request) {
        var user = authService.register(request.nickname(), request.password());
        return ResponseEntity.ok()
                .body(LoginResponse.User.builder()
                        .id(user.id())
                        .nickname(user.nickname())
                        .build());
    }

    @Log
    @PostMapping("/login")
    public ResponseEntity<LoginResponse.User> login(@RequestBody AuthRequest request,
                                                    HttpServletResponse response) {
        LoginResponse login = authService.login(request);
        attachCookie(response, ACCESS_TOKEN_COOKIE, login.accessToken(),
                true, false, "/api", "Strict", jwtAccessExpirationMs / 1_000);
        attachCookie(response, REFRESH_TOKEN_COOKIE, login.refreshToken(),
                true, false, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        attachCookie(response, DEVICE_ID_COOKIE, login.deviceId().toString(),
                true, false, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        return ResponseEntity.ok().body(login.user());
    }

    @Log
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) @Hidden String refreshToken,
                                                   HttpServletResponse response) {
        RefreshResponse refresh = refreshTokenService.refresh(refreshToken);
        attachCookie(response, ACCESS_TOKEN_COOKIE, refresh.accessToken(),
                true, false, "/api", "Strict", jwtAccessExpirationMs / 1_000);
        attachCookie(response, REFRESH_TOKEN_COOKIE, refresh.refreshToken().token(),
                true, false, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        attachCookie(response, DEVICE_ID_COOKIE, refresh.refreshToken().device().id().toString(),
                true, false, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        return ResponseEntity.ok().body(refresh);
    }

    @Log
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_TOKEN_COOKIE) String refreshToken,
                                       HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(refreshToken);
        attachCookie(response, ACCESS_TOKEN_COOKIE, "",
                true, false, "/api", "Strict", 0);
        attachCookie(response, REFRESH_TOKEN_COOKIE, "",
                true, false, "/api", "Strict", 0);
        attachCookie(response, DEVICE_ID_COOKIE, "",
                true, false, "/api", "Strict", 0);
        return ResponseEntity.ok().build();
    }
}
