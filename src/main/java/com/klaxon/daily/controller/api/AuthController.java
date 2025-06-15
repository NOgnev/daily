package com.klaxon.daily.controller.api;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.config.log.hidden.Hidden;
import com.klaxon.daily.dto.request.LoginRequest;
import com.klaxon.daily.dto.request.RegisterRequest;
import com.klaxon.daily.dto.response.LoginResponse;
import com.klaxon.daily.dto.response.RefreshResponse;
import com.klaxon.daily.service.AuthService;
import com.klaxon.daily.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.klaxon.daily.service.CookieService.attachCookie;
import static com.klaxon.daily.util.Constants.ACCESS_TOKEN_COOKIE;
import static com.klaxon.daily.util.Constants.DEVICE_ID_COOKIE;
import static com.klaxon.daily.util.Constants.REFRESH_TOKEN_COOKIE;

@Slf4j
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
    public ResponseEntity<LoginResponse.User> register(@RequestBody @Valid RegisterRequest request) {
        var user = authService.register(request.nickname(), request.password());
        return ResponseEntity.ok()
                .body(LoginResponse.User.builder()
                        .id(user.id())
                        .nickname(user.nickname())
                        .build());
    }

    @Log
    @PostMapping("/login")
    public ResponseEntity<LoginResponse.User> login(@RequestBody @Valid LoginRequest request,
                                                    @Hidden HttpServletResponse response) {
        LoginResponse login = authService.login(request);
        attachCookie(response, ACCESS_TOKEN_COOKIE, login.accessToken(),
                true, true, "/api", "Strict", jwtAccessExpirationMs / 1_000);
        attachCookie(response, REFRESH_TOKEN_COOKIE, login.refreshToken(),
                true, true, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        attachCookie(response, DEVICE_ID_COOKIE, login.deviceId().toString(),
                true, true, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        return ResponseEntity.ok().body(login.user());
    }

    @Log
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@Hidden
                                                   @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false)
                                                   String refreshToken,
                                                   @Hidden HttpServletResponse response) {
        RefreshResponse refresh = refreshTokenService.refresh(refreshToken);
        attachCookie(response, ACCESS_TOKEN_COOKIE, refresh.accessToken(),
                true, true, "/api", "Strict", jwtAccessExpirationMs / 1_000);
        attachCookie(response, REFRESH_TOKEN_COOKIE, refresh.refreshToken().token(),
                true, true, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        attachCookie(response, DEVICE_ID_COOKIE, refresh.refreshToken().device().id().toString(),
                true, true, "/api", "Strict", jwtRefreshExpirationMs / 1_000);
        return ResponseEntity.ok().body(refresh);
    }

    @Log
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = REFRESH_TOKEN_COOKIE) String refreshToken,
                                       @Hidden HttpServletResponse response) {
        refreshTokenService.deleteRefreshToken(refreshToken);
        attachCookie(response, ACCESS_TOKEN_COOKIE, "",
                true, true, "/api", "Strict", 0);
        attachCookie(response, REFRESH_TOKEN_COOKIE, "",
                true, true, "/api", "Strict", 0);
//        attachCookie(response, DEVICE_ID_COOKIE, "",
//                true, true, "/api", "Strict", 0);
        return ResponseEntity.ok().build();
    }
}
